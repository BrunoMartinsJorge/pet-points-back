package br.com.api.petpoints.domain.users.atendente.features.dashboard.service;

import br.com.api.petpoints.domain.users.atendente.features.dashboard.dto.CardsDashboardDto;

public interface AtendenteDashboardService {

    CardsDashboardDto gerarCardsDashboardAtendente(Long idUsuario);
}
