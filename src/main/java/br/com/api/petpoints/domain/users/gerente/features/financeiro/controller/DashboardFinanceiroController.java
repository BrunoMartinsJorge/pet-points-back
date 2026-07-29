package br.com.api.petpoints.domain.users.gerente.features.financeiro.controller;

import br.com.api.petpoints.domain.users.gerente.features.financeiro.dto.CardsFinanceiroDto;
import br.com.api.petpoints.domain.users.gerente.features.financeiro.dto.FaturaDto;
import br.com.api.petpoints.domain.users.gerente.features.financeiro.dto.GraficoReceitaDto;
import br.com.api.petpoints.domain.users.gerente.features.financeiro.service.DashboardFinanceiroServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/gerente/financeiro")
@RequiredArgsConstructor
public class DashboardFinanceiroController {

    private final DashboardFinanceiroServiceImpl dashboardFinanceiroService;

    @GetMapping("/cards")
    public ResponseEntity<CardsFinanceiroDto> buscarCards() {
        return ResponseEntity.ok().body(this.dashboardFinanceiroService.buscarCards());
    }

    @GetMapping("/grafico")
    public ResponseEntity<GraficoReceitaDto> buscarGrafico(@RequestParam(defaultValue = "DIA") String agrupamento) {
        return ResponseEntity.ok().body(this.dashboardFinanceiroService.buscarGrafico(agrupamento));
    }

    @GetMapping("/pagamentos")
    public ResponseEntity<List<FaturaDto>> listarFaturas() {
        return ResponseEntity.ok().body(this.dashboardFinanceiroService.listarFaturas());
    }
}
