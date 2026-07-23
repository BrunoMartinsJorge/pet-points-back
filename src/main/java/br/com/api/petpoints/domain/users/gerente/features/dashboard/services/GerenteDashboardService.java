package br.com.api.petpoints.domain.users.gerente.features.dashboard.services;

import br.com.api.petpoints.domain.users.gerente.features.dashboard.dto.AcessosMesDto;
import br.com.api.petpoints.domain.users.gerente.features.dashboard.dto.ConsultasDashboardGerenteDto;
import br.com.api.petpoints.domain.users.gerente.features.dashboard.dto.MovimentacoesDashboardGerenteDto;

import java.util.List;

public interface GerenteDashboardService {

    List<AcessosMesDto> buscarAcessosSistema();
    List<ConsultasDashboardGerenteDto> buscarConsultasAgendadas();
    List<MovimentacoesDashboardGerenteDto> buscarMovimentacoesDia();
}
