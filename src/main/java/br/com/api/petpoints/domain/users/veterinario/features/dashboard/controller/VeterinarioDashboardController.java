package br.com.api.petpoints.domain.users.veterinario.features.dashboard.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.domain.users.veterinario.features.dashboard.dto.AvaliacoesVeterinarioDashboardDto;
import br.com.api.petpoints.domain.users.veterinario.features.dashboard.dto.CardsVeterinarioDashboardDto;
import br.com.api.petpoints.domain.users.veterinario.features.dashboard.dto.ConsultasVeterinaiosDashboardDto;
import br.com.api.petpoints.domain.users.veterinario.features.dashboard.service.VeterinarioDashboardServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/veterinario/dashboard")
public class VeterinarioDashboardController {

    private final VeterinarioDashboardServiceImpl veterinarioDashboardService;

    private Long getIdUsuario(HttpServletRequest request) {
        return new TokenModel(request.getHeader("Authorization")).getIdUsuario();
    }

    @GetMapping("/cards")
    public ResponseEntity<CardsVeterinarioDashboardDto> gerarCardsVeterinarioDashboard(HttpServletRequest request) {
        return ResponseEntity.ok().body(this.veterinarioDashboardService.buscarCardsVeterinario(this.getIdUsuario(request)));
    }

    @GetMapping("/consultas-dia")
    public ResponseEntity<List<ConsultasVeterinaiosDashboardDto>> buscarConsultasDiarias(HttpServletRequest request) {
        return ResponseEntity.ok().body(this.veterinarioDashboardService.buscarConsultasVeterinario(this.getIdUsuario(request)));
    }

    @GetMapping("/avaliacoes")
    public ResponseEntity<List<AvaliacoesVeterinarioDashboardDto>> buscarAvaliacoesVeterinario(HttpServletRequest request) {
        return ResponseEntity.ok().body(this.veterinarioDashboardService.buscarAvaliacoesVeterinario(this.getIdUsuario(request)));
    }
}
