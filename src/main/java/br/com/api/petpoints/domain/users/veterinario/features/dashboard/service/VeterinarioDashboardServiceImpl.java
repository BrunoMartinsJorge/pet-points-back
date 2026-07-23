package br.com.api.petpoints.domain.users.veterinario.features.dashboard.service;

import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.domain.users.veterinario.features.dashboard.dto.AvaliacoesVeterinarioDashboardDto;
import br.com.api.petpoints.domain.users.veterinario.features.dashboard.dto.CardsVeterinarioDashboardDto;
import br.com.api.petpoints.domain.users.veterinario.features.dashboard.dto.ConsultasVeterinaiosDashboardDto;
import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.models.AvaliacaoModel;
import br.com.api.petpoints.shared.models.ConsultaModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VeterinarioDashboardServiceImpl implements VeterinarioDashboardService {

    private final UsuarioRepository usuarioRepository;
    private final ConsultaRepository consultaRepository;

    @Override
    public CardsVeterinarioDashboardDto buscarCardsVeterinario(Long idUsuario) {
        UsuarioModel veterinario = this.usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: " + idUsuario + " não encontrado!"));
        log.info("[VETERINARIO] - Veterinario: {}", veterinario);
        List<ConsultaModel> consultasVeterinario = this.consultaRepository.findAllByVeterinario_Id(idUsuario);
        List<ConsultaModel> consultas = this.consultaRepository.buscarAvaliacoesFinalizadas();
        Map<UsuarioModel, Double> mediaPorVeterinario = consultas.stream()
                .filter(a -> a.getAvaliacao() != null)
                .collect(Collectors.groupingBy(
                        ConsultaModel::getAtendente,
                        Collectors.averagingInt(a -> a.getAvaliacao().getPontuacao())
                ));
        List<UsuarioModel> ranking = mediaPorVeterinario.entrySet().stream()
                .sorted(Map.Entry.<UsuarioModel, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();
        int classificacao = ranking.indexOf(veterinario) + 1;
        int total = consultasVeterinario.size();
        int finalizadas = consultasVeterinario.stream().filter(consulta -> consulta.getStatus().equals(StatusConsultaEnum.FINALIZADO)).toList().size();
        log.info("[VETERINARIO] - Classificacao: {}º", classificacao);
        log.info("[VETERINARIO] - Total de Consultas: {}", total);
        log.info("[VETERINARIO] - Consultas Finalizadas: {}º", finalizadas);
        return new CardsVeterinarioDashboardDto(finalizadas, total, classificacao);
    }

    @Override
    public List<ConsultasVeterinaiosDashboardDto> buscarConsultasVeterinario(Long idUsuario) {
        List<ConsultaModel> consultas = this.consultaRepository.findAllByVeterinario_Id(idUsuario).stream().filter(consulta ->
                consulta.getDataConsulta().toLocalDate().equals(LocalDate.now())).toList();
        return ConsultasVeterinaiosDashboardDto.convert(consultas);
    }

    @Override
    public List<AvaliacoesVeterinarioDashboardDto> buscarAvaliacoesVeterinario(Long idUsuario) {
        List<AvaliacaoModel> avaliacoes = this.consultaRepository.findAllByVeterinario_IdAndAvaliacaoIsNotNull(idUsuario).stream().map(ConsultaModel::getAvaliacao).toList();
        return AvaliacoesVeterinarioDashboardDto.convert(avaliacoes);
    }
}
