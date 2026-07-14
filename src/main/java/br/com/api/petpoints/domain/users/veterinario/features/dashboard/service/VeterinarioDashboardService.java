package br.com.api.petpoints.domain.users.veterinario.features.dashboard.service;

import br.com.api.petpoints.domain.users.veterinario.features.dashboard.dto.AvaliacoesVeterinarioDashboardDto;
import br.com.api.petpoints.domain.users.veterinario.features.dashboard.dto.CardsVeterinarioDashboardDto;
import br.com.api.petpoints.domain.users.veterinario.features.dashboard.dto.ConsultasVeterinaiosDashboardDto;

import java.util.List;

public interface VeterinarioDashboardService {

    CardsVeterinarioDashboardDto buscarCardsVeterinario(Long idUsuario);
    List<ConsultasVeterinaiosDashboardDto> buscarConsultasVeterinario(Long idUsuario);
    List<AvaliacoesVeterinarioDashboardDto> buscarAvaliacoesVeterinario(Long idUsuario);
}
