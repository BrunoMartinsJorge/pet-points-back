package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto.ConsultaDto;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.service.MinhasConsultaVeterinarioServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/veterinario/minhas-consultas")
public class MinhasConsultasVeterinarioController {

    private final MinhasConsultaVeterinarioServiceImpl minhasConsultaVeterinarioService;

    @GetMapping
    public ResponseEntity<List<ConsultaDto>> listarConsultasPorVeterinario(HttpServletRequest request) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok().body(this.minhasConsultaVeterinarioService.listarMinhasConsultas(token.getIdUsuario()));
    }

    @GetMapping("/hoje")
    public ResponseEntity<List<ConsultaDto>> listarConsultasDoDia(HttpServletRequest request) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok().body(this.minhasConsultaVeterinarioService.listarMinhasConsultasDoDia(token.getIdUsuario()));
    }

    @PutMapping("/iniciar/{id}")
    public ResponseEntity<Void> iniciarConsulta(HttpServletRequest request, @PathVariable Long id) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        this.minhasConsultaVeterinarioService.iniciarConsulta(token.getIdUsuario(), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/finalizar/{id}")
    public ResponseEntity<Void> finalizarConsulta(HttpServletRequest request, @PathVariable Long id, @RequestBody String resumo) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        this.minhasConsultaVeterinarioService.finalizarConsulta(token.getIdUsuario(), id, resumo);
        return ResponseEntity.ok().build();
    }
}
