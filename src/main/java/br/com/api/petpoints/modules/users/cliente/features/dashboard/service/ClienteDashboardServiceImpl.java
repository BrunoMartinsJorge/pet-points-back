package br.com.api.petpoints.modules.users.cliente.features.dashboard.service;

import br.com.api.petpoints.modules.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.modules.users.cliente.features.dashboard.dto.AtendimentosPendentesDto;
import br.com.api.petpoints.modules.users.cliente.features.dashboard.dto.ConsultaDashboardDto;
import br.com.api.petpoints.modules.users.cliente.features.dashboard.dto.PagamentosPendentesDto;
import br.com.api.petpoints.shared.enums.StatusAtendimentoEnum;
import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.models.*;
import br.com.api.petpoints.shared.repository.AtendimentoRepository;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteDashboardServiceImpl implements ClienteDashboardService {

    private final ConsultaRepository consultaRepository;
    private final AtendimentoRepository atendimentoRepository;

    @Override
    public List<ConsultaDashboardDto> listarConsultasPendentes(Long idUsuario) {
        List<ConsultaModel> consultas = this.consultaRepository.findAllBySolicitante_IdAndStatus(idUsuario, StatusConsultaEnum.PENDENTE);
        return ConsultaDashboardDto.convert(consultas);
    }

    @Override
    public List<ConsultaDashboardDto> listarConsultasIniciadasAprovadas(Long idUsuario) {
        List<ConsultaModel> consultas = this.consultaRepository.buscarConsultasConfirmadasPorUsuario(idUsuario);
        return ConsultaDashboardDto.convert(consultas);
    }

    @Override
    public List<AtendimentosPendentesDto> listarAtendimentosPendentes(Long idUsuario) {
        List<AtendimentoModel> atendimento = this.atendimentoRepository.findAllByCliente_IdAndStatus(idUsuario, StatusAtendimentoEnum.PENDENTE);
        return AtendimentosPendentesDto.convert(atendimento);
    }

    @Override
    public List<PagamentosPendentesDto> listarPagamentosPendentes(Long idUsuario) {
        List<ConsultaModel> consultas = this.consultaRepository.buscarConsultasPorUsuarioComPagamentosPendentes(idUsuario);
        return PagamentosPendentesDto.convert(consultas);
    }

}
