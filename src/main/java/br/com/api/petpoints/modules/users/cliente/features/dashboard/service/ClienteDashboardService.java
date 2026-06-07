package br.com.api.petpoints.modules.users.cliente.features.dashboard.service;

import br.com.api.petpoints.modules.users.cliente.features.dashboard.dto.AtendimentosPendentesDto;
import br.com.api.petpoints.modules.users.cliente.features.dashboard.dto.ConsultaDashboardDto;
import br.com.api.petpoints.modules.users.cliente.features.dashboard.dto.PagamentosPendentesDto;

import java.util.List;

public interface ClienteDashboardService {
    List<PagamentosPendentesDto> listarPagamentosPendentes(Long idUsuario);
    List<ConsultaDashboardDto> listarConsultasIniciadasAprovadas(Long idUsuario);
    List<AtendimentosPendentesDto> listarAtendimentosPendentes(Long idUsuario);
}
