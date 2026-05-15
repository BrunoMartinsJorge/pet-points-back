package br.com.api.petpoints.modules.users.gerente.features.dashboard.controller;

import br.com.api.petpoints.modules.users.gerente.features.dashboard.dto.AcessosMesDto;
import br.com.api.petpoints.modules.users.gerente.features.dashboard.dto.ConsultasDashboardGerenteDto;
import br.com.api.petpoints.modules.users.gerente.features.dashboard.dto.MovimentacoesDashboardGerenteDto;
import br.com.api.petpoints.modules.users.gerente.features.dashboard.services.GerenteDashboardServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/gerente/dashboard")
@RequiredArgsConstructor
public class GerenteDashboardController {

    private final GerenteDashboardServiceImpl gerenteDashboardService;

    @GetMapping
    public ResponseEntity<List<AcessosMesDto>> listarAcessosPorMes() {
        return ResponseEntity.ok().body(this.gerenteDashboardService.buscarAcessosSistema());
    }

    @GetMapping("/consultas")
    public ResponseEntity<List<ConsultasDashboardGerenteDto>> buscarConsultas() {
        return ResponseEntity.ok().body(this.gerenteDashboardService.buscarConsultasAgendadas());
    }

    @GetMapping("/movimentacoes")
    public ResponseEntity<List<MovimentacoesDashboardGerenteDto>> buscarMovimentacoes() {
        return ResponseEntity.ok().body(this.gerenteDashboardService.buscarMovimentacoesDia());
    }
}
