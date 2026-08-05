package br.com.api.petpoints.domain.users.atendente.features.dashboard.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.domain.users.atendente.features.dashboard.dto.CardsDashboardDto;
import br.com.api.petpoints.domain.users.atendente.features.dashboard.service.AtendenteDashboardServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/atendente/dashboard")
@RequiredArgsConstructor
public class AtendenteDashboardController {

    private final AtendenteDashboardServiceImpl atendenteDashboardService;

    @GetMapping("/cards")
    public ResponseEntity<CardsDashboardDto> buscarCardsDashboardAtendente(HttpServletRequest request) {
        return ResponseEntity.ok().body(this.atendenteDashboardService.gerarCardsDashboardAtendente(new TokenModel(request.getHeader("Authorization")).getIdUsuario()));
    }
}
