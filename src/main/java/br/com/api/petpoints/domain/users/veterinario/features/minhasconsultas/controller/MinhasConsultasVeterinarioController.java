package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto.ConsultaAtualDto;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto.ConsultaVeterinarioDto;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto.InformacoesConsultaSelecionadaDto;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto.ProdutoCobrancaDto;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.forms.FinalizarConsultaForm;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.forms.PrescricaoForm;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.service.MinhasConsultaVeterinarioServiceImpl;
import br.com.api.petpoints.shared.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    @GetMapping("/produtos-cobranca")
    public ResponseEntity<List<ProdutoCobrancaDto>> listarProdutosParaCobranca() {
        return ResponseEntity.ok().body(this.minhasConsultaVeterinarioService.listarProdutosParaCobranca());
    }

    @PutMapping("/finalizar/{id}")
    public ResponseEntity<Void> finalizarConsulta(HttpServletRequest request, @PathVariable Long id, @RequestBody FinalizarConsultaForm form) {
        this.minhasConsultaVeterinarioService.finalizarConsulta(this.getIdUsuario(request), id, form);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/prescricao")
    public ResponseEntity<byte[]> gerarPrescricaoConsulta(@RequestBody PrescricaoForm payload, HttpServletRequest request) {
        byte[] pdf = this.minhasConsultaVeterinarioService.gerarPrescricao(TokenUtils.getIdUsuario(request), payload);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=prescricao-consulta.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}

