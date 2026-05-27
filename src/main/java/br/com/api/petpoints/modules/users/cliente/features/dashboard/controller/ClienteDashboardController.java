package br.com.api.petpoints.modules.users.cliente.features.dashboard.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.modules.users.cliente.features.dashboard.dto.AtendimentosPendentesDto;
import br.com.api.petpoints.modules.users.cliente.features.dashboard.dto.ConsultaDashboardDto;
import br.com.api.petpoints.modules.users.cliente.features.dashboard.service.ClienteDashboardServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cliente/dashboard")
@RequiredArgsConstructor
public class ClienteDashboardController {

    private final ClienteDashboardServiceImpl clienteDashboardService;

    @GetMapping("/consultas-agendadas")
    public ResponseEntity<List<ConsultaDashboardDto>> buscarConsultasAgendadas(HttpServletRequest request) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok().body(this.clienteDashboardService.listarConsultasIniciadasAprovadas(token.getIdUsuario()));
    }

    @GetMapping("/atendimentos-pendentes")
    public ResponseEntity<List<AtendimentosPendentesDto>> buscarAtendimentosPendentes(HttpServletRequest request) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok().body(this.clienteDashboardService.listarAtendimentosPendentes(token.getIdUsuario()));
    }

    @GetMapping("/consultas-pendentes")
    public ResponseEntity<List<ConsultaDashboardDto>> buscarConsultasPendentes(HttpServletRequest request) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok().body(this.clienteDashboardService.listarConsultasPendentes(token.getIdUsuario()));
    }
}
