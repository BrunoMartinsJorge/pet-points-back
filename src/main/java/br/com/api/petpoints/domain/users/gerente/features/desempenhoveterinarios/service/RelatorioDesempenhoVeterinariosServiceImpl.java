package br.com.api.petpoints.domain.users.gerente.features.desempenhoveterinarios.service;

import br.com.api.petpoints.domain.users.gerente.features.desempenhoveterinarios.dto.DesempenhoVeterinarioDto;
import br.com.api.petpoints.domain.users.gerente.features.desempenhoveterinarios.dto.ResumoDesempenhoVeterinariosDto;
import br.com.api.petpoints.domain.users.gerente.features.desempenhoveterinarios.form.RelatorioDesempenhoVeterinariosForm;
import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.models.AvaliacaoModel;
import br.com.api.petpoints.shared.models.ConsultaModel;
import br.com.api.petpoints.shared.models.EspecializacaoModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import br.com.api.petpoints.shared.repository.EspecializacaoRepository;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import br.com.api.petpoints.shared.utils.LocalDateUtils;
import br.com.api.petpoints.shared.utils.RelatoriosUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RelatorioDesempenhoVeterinariosServiceImpl implements RelatorioDesempenhoVeterinariosService {

    private static final int MINIMO_AVALIACOES_DESTAQUE = 3;

    private final ConsultaRepository consultaRepository;
    private final EspecializacaoRepository especializacaoRepository;
    private final TemplateEngine templateEngine;

    @Override
    public byte[] gerarRelatorio(RelatorioDesempenhoVeterinariosForm form) {
        List<ConsultaModel> consultas = this.filtrarConsultas(form, this.consultaRepository.findAll());

        Map<UsuarioModel, List<ConsultaModel>> consultasPorVeterinario = consultas.stream()
                .filter(consulta -> consulta.getVeterinario() != null)
                .collect(Collectors.groupingBy(ConsultaModel::getVeterinario));

        List<DesempenhoVeterinarioDto> desempenho = consultasPorVeterinario.entrySet().stream()
                .map(entry -> this.montarDesempenho(entry.getKey(), entry.getValue()))
                .sorted(Comparator
                        .comparing((DesempenhoVeterinarioDto dto) -> dto.getNotaMedia() == null ? -1.0 : dto.getNotaMedia())
                        .reversed()
                        .thenComparing(Comparator.comparingInt(DesempenhoVeterinarioDto::getFinalizadas).reversed()))
                .toList();

        ResumoDesempenhoVeterinariosDto resumo = this.montarResumo(consultas, desempenho);

        Context context = new Context();
        context.setVariable("periodo", this.formatarPeriodo(form));
        context.setVariable("dataGeracao", LocalDateTimeUtils.converterLocalDateTimeParaPtBr(LocalDateTime.now()));
        context.setVariable("resumo", resumo);
        context.setVariable("desempenho", desempenho);

        String html = this.templateEngine.process("relatorios/RelatorioDesempenhoVeterinarios", context);
        return RelatoriosUtils.getBytes(html);
    }

    private List<ConsultaModel> filtrarConsultas(RelatorioDesempenhoVeterinariosForm form, List<ConsultaModel> consultas) {
        Stream<ConsultaModel> stream = consultas.stream().filter(consulta -> consulta.getSolicitadoEm() != null);

        if (form.getDataInicio() != null)
            stream = stream.filter(consulta -> !consulta.getSolicitadoEm().toLocalDate().isBefore(form.getDataInicio()));

        if (form.getDataFim() != null)
            stream = stream.filter(consulta -> !consulta.getSolicitadoEm().toLocalDate().isAfter(form.getDataFim()));

        if (form.getIdVeterinario() != null)
            stream = stream.filter(consulta -> consulta.getVeterinario() != null && Objects.equals(consulta.getVeterinario().getId(), form.getIdVeterinario()));

        return stream.toList();
    }

    private DesempenhoVeterinarioDto montarDesempenho(UsuarioModel veterinario, List<ConsultaModel> consultas) {
        int total = consultas.size();
        int finalizadas = (int) consultas.stream().filter(consulta -> consulta.getStatus() == StatusConsultaEnum.FINALIZADO).count();
        int canceladas = (int) consultas.stream().filter(consulta -> consulta.getStatus() == StatusConsultaEnum.CANCELADO).count();
        int reprovadas = (int) consultas.stream().filter(consulta -> consulta.getStatus() == StatusConsultaEnum.REPROVADA).count();
        double taxaConclusao = total == 0 ? 0 : (finalizadas / (double) total) * 100;

        List<AvaliacaoModel> avaliacoes = consultas.stream()
                .map(ConsultaModel::getAvaliacao)
                .filter(Objects::nonNull)
                .toList();

        Double notaMedia = avaliacoes.isEmpty() ? null :
                avaliacoes.stream().mapToInt(AvaliacaoModel::getPontuacao).average().orElse(0);

        Double tempoMedioAtendimentoMinutos = this.calcularTempoMedioAtendimento(consultas);

        String especializacoes = this.especializacaoRepository.buscarPorVeterinario(veterinario).stream()
                .map(EspecializacaoModel::getDescricao)
                .collect(Collectors.joining(", "));

        return new DesempenhoVeterinarioDto(
                veterinario.getId(),
                veterinario.getNome(),
                especializacoes.isEmpty() ? "-" : especializacoes,
                total,
                finalizadas,
                canceladas,
                reprovadas,
                taxaConclusao,
                notaMedia,
                avaliacoes.size(),
                tempoMedioAtendimentoMinutos
        );
    }

    private Double calcularTempoMedioAtendimento(List<ConsultaModel> consultas) {
        List<Long> duracoes = consultas.stream()
                .filter(consulta -> consulta.getStatus() == StatusConsultaEnum.FINALIZADO)
                .filter(consulta -> consulta.getIniciadoEm() != null && consulta.getFinalizadoEm() != null)
                .map(consulta -> Duration.between(consulta.getIniciadoEm(), consulta.getFinalizadoEm()).toMinutes())
                .toList();

        return duracoes.isEmpty() ? null : duracoes.stream().mapToLong(Long::longValue).average().orElse(0);
    }

    private ResumoDesempenhoVeterinariosDto montarResumo(List<ConsultaModel> consultas, List<DesempenhoVeterinarioDto> desempenho) {
        int total = consultas.size();
        int finalizadas = (int) consultas.stream().filter(consulta -> consulta.getStatus() == StatusConsultaEnum.FINALIZADO).count();
        int canceladas = (int) consultas.stream().filter(consulta -> consulta.getStatus() == StatusConsultaEnum.CANCELADO).count();
        int reprovadas = (int) consultas.stream().filter(consulta -> consulta.getStatus() == StatusConsultaEnum.REPROVADA).count();

        double taxaConclusaoGeral = total == 0 ? 0 : (finalizadas / (double) total) * 100;
        double taxaCancelamentoGeral = total == 0 ? 0 : (canceladas / (double) total) * 100;
        double taxaReprovacaoGeral = total == 0 ? 0 : (reprovadas / (double) total) * 100;

        List<AvaliacaoModel> avaliacoes = consultas.stream()
                .map(ConsultaModel::getAvaliacao)
                .filter(Objects::nonNull)
                .toList();

        Double notaMediaGeral = avaliacoes.isEmpty() ? null :
                avaliacoes.stream().mapToInt(AvaliacaoModel::getPontuacao).average().orElse(0);

        DesempenhoVeterinarioDto destaque = desempenho.stream()
                .filter(dto -> dto.getNotaMedia() != null && dto.getQuantidadeAvaliacoes() >= MINIMO_AVALIACOES_DESTAQUE)
                .max(Comparator.comparingDouble(DesempenhoVeterinarioDto::getNotaMedia))
                .orElse(null);

        return new ResumoDesempenhoVeterinariosDto(
                total,
                finalizadas,
                canceladas,
                reprovadas,
                taxaConclusaoGeral,
                taxaCancelamentoGeral,
                taxaReprovacaoGeral,
                notaMediaGeral,
                avaliacoes.size(),
                destaque == null ? null : destaque.getNome(),
                destaque == null ? null : destaque.getNotaMedia()
        );
    }

    private String formatarPeriodo(RelatorioDesempenhoVeterinariosForm form) {
        if (form.getDataInicio() == null && form.getDataFim() == null) return "Todo o período";
        String inicio = form.getDataInicio() != null ? LocalDateUtils.converterLocalDateParaPtBr(form.getDataInicio()) : "Início";
        String fim = form.getDataFim() != null ? LocalDateUtils.converterLocalDateParaPtBr(form.getDataFim()) : "Hoje";
        return inicio + " até " + fim;
    }
}
