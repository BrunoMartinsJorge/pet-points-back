package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto.ConsultaAtualDto;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto.ConsultaVeterinarioDto;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto.InformacoesConsultaSelecionadaDto;
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

    private Long getIdUsuario(HttpServletRequest request) {
        return new TokenModel(request.getHeader("Authorization")).getIdUsuario();
    }

    @GetMapping
    public ResponseEntity<List<ConsultaVeterinarioDto>> listarConsultasPorVeterinario(HttpServletRequest request) {
        return ResponseEntity.ok().body(this.minhasConsultaVeterinarioService.listarMinhasConsultas(this.getIdUsuario(request)));
    }

    @GetMapping("/hoje")
    public ResponseEntity<List<ConsultaVeterinarioDto>> listarConsultasDoDia(HttpServletRequest request) {
        return ResponseEntity.ok().body(this.minhasConsultaVeterinarioService.listarMinhasConsultasDoDia(this.getIdUsuario(request)));
    }

    @GetMapping("/consulta-atual")
    public ResponseEntity<ConsultaAtualDto> buscarConsultaAtualVeterinario(HttpServletRequest request) {
        return ResponseEntity.ok().body(this.minhasConsultaVeterinarioService.buscarConsultaAtualVeterinario(this.getIdUsuario(request)));
    }

    @GetMapping("/selecionar-consulta/{idConsulta}")
    public ResponseEntity<InformacoesConsultaSelecionadaDto> buscarInformacoesConsulta(@PathVariable Long idConsulta, HttpServletRequest request) {
        return ResponseEntity.ok().body(this.minhasConsultaVeterinarioService.buscarInformacoesConsulta(idConsulta, this.getIdUsuario(request)));
    }

    @PutMapping("/iniciar/{id}")
    public ResponseEntity<Void> iniciarConsulta(HttpServletRequest request, @PathVariable Long id) {
        this.minhasConsultaVeterinarioService.iniciarConsulta(this.getIdUsuario(request), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/finalizar/{id}")
    public ResponseEntity<Void> finalizarConsulta(HttpServletRequest request, @PathVariable Long id, @RequestBody String resumo) {
        this.minhasConsultaVeterinarioService.finalizarConsulta(this.getIdUsuario(request), id, resumo);
        return ResponseEntity.ok().build();
    }
}
