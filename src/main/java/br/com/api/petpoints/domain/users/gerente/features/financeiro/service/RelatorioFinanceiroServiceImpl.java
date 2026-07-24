package br.com.api.petpoints.domain.users.gerente.features.financeiro.service;

import br.com.api.petpoints.domain.users.gerente.features.financeiro.dto.PagamentoRelatorioDto;
import br.com.api.petpoints.domain.users.gerente.features.financeiro.dto.ReceitaPorTipoPagamentoDto;
import br.com.api.petpoints.domain.users.gerente.features.financeiro.dto.ResumoFinanceiroDto;
import br.com.api.petpoints.domain.users.gerente.features.financeiro.form.RelatorioFinanceiroForm;
import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import br.com.api.petpoints.shared.models.PagamentoModel;
import br.com.api.petpoints.shared.repository.PagamentoRepository;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import br.com.api.petpoints.shared.utils.LocalDateUtils;
import br.com.api.petpoints.shared.utils.RelatoriosUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RelatorioFinanceiroServiceImpl implements RelatorioFinanceiroService {

    private final PagamentoRepository pagamentoRepository;
    private final TemplateEngine templateEngine;

    @Override
    public byte[] gerarRelatorio(RelatorioFinanceiroForm form) {
        List<PagamentoModel> pagamentos = this.filtrarPagamentos(form, this.pagamentoRepository.findAll());

        ResumoFinanceiroDto resumo = this.montarResumo(pagamentos, form);
        List<ReceitaPorTipoPagamentoDto> receitaPorTipo = this.montarReceitaPorTipo(pagamentos, resumo.getReceitaTotal());
        List<PagamentoRelatorioDto> detalhes = PagamentoRelatorioDto.convert(
                pagamentos.stream()
                        .sorted(Comparator.comparing(PagamentoModel::getDataPagamento).reversed())
                        .toList()
        );

        Context context = new Context();
        context.setVariable("periodo", this.formatarPeriodo(form));
        context.setVariable("dataGeracao", LocalDateTimeUtils.converterLocalDateTimeParaPtBr(LocalDateTime.now()));
        context.setVariable("resumo", resumo);
        context.setVariable("receitaPorTipo", receitaPorTipo);
        context.setVariable("pagamentos", detalhes);

        String html = this.templateEngine.process("relatorios/RelatorioFinanceiro", context);
        return RelatoriosUtils.getBytes(html);
    }

    private List<PagamentoModel> filtrarPagamentos(RelatorioFinanceiroForm form, List<PagamentoModel> pagamentos) {
        Stream<PagamentoModel> stream = pagamentos.stream().filter(pagamento -> pagamento.getDataPagamento() != null);

        if (form.getDataInicio() != null)
            stream = stream.filter(pagamento -> !pagamento.getDataPagamento().toLocalDate().isBefore(form.getDataInicio()));

        if (form.getDataFim() != null)
            stream = stream.filter(pagamento -> !pagamento.getDataPagamento().toLocalDate().isAfter(form.getDataFim()));

        if (form.getTipoPagamento() != null && !form.getTipoPagamento().isEmpty())
            stream = stream.filter(pagamento -> pagamento.getTipoPagamento() != null && pagamento.getTipoPagamento().toString().equals(form.getTipoPagamento()));

        if (form.getStatusPagamento() != null && !form.getStatusPagamento().isEmpty())
            stream = stream.filter(pagamento -> pagamento.getStatusPagamento().toString().equals(form.getStatusPagamento()));

        return stream.toList();
    }

    private ResumoFinanceiroDto montarResumo(List<PagamentoModel> pagamentos, RelatorioFinanceiroForm form) {
        List<PagamentoModel> aprovados = pagamentos.stream()
                .filter(pagamento -> pagamento.getStatusPagamento() == StatusPagamentoEnum.APROVADO)
                .toList();

        double receitaTotal = aprovados.stream().mapToDouble(PagamentoModel::getValorPagamento).sum();
        double ticketMedio = aprovados.isEmpty() ? 0 : receitaTotal / aprovados.size();

        LocalDate hoje = LocalDate.now();

        double valorAReceber = pagamentos.stream()
                .filter(this::isPendente)
                .filter(pagamento -> pagamento.getDataLimitePagamento() == null || !pagamento.getDataLimitePagamento().toLocalDate().isBefore(hoje))
                .mapToDouble(PagamentoModel::getValorPagamento).sum();

        double valorEmAtraso = pagamentos.stream()
                .filter(this::isPendente)
                .filter(pagamento -> pagamento.getDataLimitePagamento() != null && pagamento.getDataLimitePagamento().toLocalDate().isBefore(hoje))
                .mapToDouble(PagamentoModel::getValorPagamento).sum();

        double valorRecusado = pagamentos.stream()
                .filter(pagamento -> pagamento.getStatusPagamento() == StatusPagamentoEnum.REPROVADO)
                .mapToDouble(PagamentoModel::getValorPagamento).sum();

        Double variacaoPercentual = this.calcularVariacaoPercentual(form, receitaTotal);

        return new ResumoFinanceiroDto(receitaTotal, ticketMedio, valorAReceber, valorEmAtraso, valorRecusado,
                pagamentos.size(), aprovados.size(), variacaoPercentual);
    }

    private boolean isPendente(PagamentoModel pagamento) {
        return pagamento.getStatusPagamento() == StatusPagamentoEnum.PENDENTE || pagamento.getStatusPagamento() == StatusPagamentoEnum.ENVIADO;
    }

    private Double calcularVariacaoPercentual(RelatorioFinanceiroForm form, double receitaAtual) {
        if (form.getDataInicio() == null || form.getDataFim() == null) return null;

        long dias = ChronoUnit.DAYS.between(form.getDataInicio(), form.getDataFim()) + 1;
        LocalDate inicioAnterior = form.getDataInicio().minusDays(dias);
        LocalDate fimAnterior = form.getDataInicio().minusDays(1);

        double receitaAnterior = this.pagamentoRepository.findAll().stream()
                .filter(pagamento -> pagamento.getStatusPagamento() == StatusPagamentoEnum.APROVADO)
                .filter(pagamento -> pagamento.getDataPagamento() != null)
                .filter(pagamento -> !pagamento.getDataPagamento().toLocalDate().isBefore(inicioAnterior)
                        && !pagamento.getDataPagamento().toLocalDate().isAfter(fimAnterior))
                .mapToDouble(PagamentoModel::getValorPagamento).sum();

        if (receitaAnterior == 0) return null;
        return ((receitaAtual - receitaAnterior) / receitaAnterior) * 100;
    }

    private List<ReceitaPorTipoPagamentoDto> montarReceitaPorTipo(List<PagamentoModel> pagamentos, double receitaTotal) {
        Map<TipoPagamentoEnum, Double> agrupado = pagamentos.stream()
                .filter(pagamento -> pagamento.getStatusPagamento() == StatusPagamentoEnum.APROVADO && pagamento.getTipoPagamento() != null)
                .collect(Collectors.groupingBy(PagamentoModel::getTipoPagamento, Collectors.summingDouble(PagamentoModel::getValorPagamento)));

        return agrupado.entrySet().stream()
                .map(entry -> new ReceitaPorTipoPagamentoDto(
                        entry.getKey().getDescricao(),
                        entry.getValue(),
                        receitaTotal == 0 ? 0 : (entry.getValue() / receitaTotal) * 100))
                .sorted(Comparator.comparingDouble(ReceitaPorTipoPagamentoDto::getValor).reversed())
                .toList();
    }

    private String formatarPeriodo(RelatorioFinanceiroForm form) {
        if (form.getDataInicio() == null && form.getDataFim() == null) return "Todo o período";
        String inicio = form.getDataInicio() != null ? LocalDateUtils.converterLocalDateParaPtBr(form.getDataInicio()) : "Início";
        String fim = form.getDataFim() != null ? LocalDateUtils.converterLocalDateParaPtBr(form.getDataFim()) : "Hoje";
        return inicio + " até " + fim;
    }
}
