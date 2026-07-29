package br.com.api.petpoints.domain.users.gerente.features.financeiro.service;

import br.com.api.petpoints.domain.users.gerente.features.financeiro.dto.CardsFinanceiroDto;
import br.com.api.petpoints.domain.users.gerente.features.financeiro.dto.FaturaDto;
import br.com.api.petpoints.domain.users.gerente.features.financeiro.dto.GraficoReceitaDto;

import java.util.List;

public interface DashboardFinanceiroService {
    CardsFinanceiroDto buscarCards();
    GraficoReceitaDto buscarGrafico(String agrupamento);
    List<FaturaDto> listarFaturas();
}
