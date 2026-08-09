package br.com.api.petpoints.domain.users.atendente.features.dashboard.service;

import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.domain.users.atendente.features.dashboard.dto.CardsDashboardDto;
import br.com.api.petpoints.shared.enums.StatusAtendimentoEnum;
import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.features.perfil.dto.RankingFuncionarioDto;
import br.com.api.petpoints.shared.models.AtendimentoModel;
import br.com.api.petpoints.shared.models.AvaliacaoModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.AtendimentoRepository;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AtendenteDashboardServiceImpl implements AtendenteDashboardService {

    private final UsuarioRepository usuarioRepository;
    private final ConsultaRepository consultaRepository;
    private final AtendimentoRepository atendimentoRepository;


    @Override
    public CardsDashboardDto gerarCardsDashboardAtendente(Long idUsuario) {
        Long atendimentosFinalizados = this.getQuantidadeAtendimentosFinalizados(idUsuario);
        Long consultasParticipadas = this.geConsultasParticipadas(idUsuario);
        Long rankingAtendente = this.gerarAvaliacaoRankingAtendente(idUsuario);
        return new CardsDashboardDto(atendimentosFinalizados, consultasParticipadas, rankingAtendente);
    }

    private Long getQuantidadeAtendimentosFinalizados(Long idUsuario) {
        return this.atendimentoRepository.findAllByAtendente_Id(idUsuario).stream().filter(atendimento ->
                atendimento.getStatus().equals(StatusAtendimentoEnum.FINALIZADO)).count();
    }

    private Long geConsultasParticipadas(Long idUsuario) {
        return (long) this.consultaRepository.findAllByAtendente_Id(idUsuario).size();
    }

    private Long gerarAvaliacaoRankingAtendente(Long idUsuario) {
        UsuarioModel atendente = this.usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioNaoEncontrado("Atendente não encontrado com o ID: " + idUsuario));
        List<AtendimentoModel> atendimentos = this.atendimentoRepository.buscarAvaliacoesFinalizadas(StatusAtendimentoEnum.FINALIZADO);
        Map<UsuarioModel, Double> mediaPorAtendente = atendimentos.stream()
                .filter(a -> a.getAvaliacao() != null)
                .collect(Collectors.groupingBy(
                        AtendimentoModel::getAtendente,
                        Collectors.averagingInt(a -> a.getAvaliacao().getPontuacao())
                ));
        List<UsuarioModel> ranking = mediaPorAtendente.entrySet().stream()
                .sorted(Map.Entry.<UsuarioModel, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();
        return (long) (ranking.indexOf(atendente) + 1);
    }
}
