package br.com.api.petpoints.modules.cliente.features.minhasconsultas.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.modules.cliente.features.minhasconsultas.dto.MinhasConsultasDto;
import br.com.api.petpoints.modules.cliente.features.minhasconsultas.forms.CancelarConsultaForm;
import br.com.api.petpoints.modules.cliente.features.minhasconsultas.forms.SolicitacaoConsultaForm;
import br.com.api.petpoints.modules.cliente.features.minhasconsultas.service.MinhasConsultasClienteServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cliente/minhas-consultas")
@RequiredArgsConstructor
public class MinhasConsultasClienteController {

    private final MinhasConsultasClienteServiceImpl minhasConsultasClienteServiceImpl;

    @GetMapping
    public ResponseEntity<List<MinhasConsultasDto>> listarMinhasConsultas(HttpServletRequest request) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok(this.minhasConsultasClienteServiceImpl.listarMinhasConsultas(token.getIdUsuario()));
    }

    @PostMapping("/solicitar")
    public ResponseEntity<MinhasConsultasDto> solicitarMinhasConsulta(HttpServletRequest request, @RequestBody @Valid SolicitacaoConsultaForm form) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok(this.minhasConsultasClienteServiceImpl.solicitarNovaConsulta(token.getIdUsuario(), form));
    }

    @PutMapping("/cancelar-consulta")
    public ResponseEntity<?> cancelarConsulta(HttpServletRequest request, @RequestBody @Valid CancelarConsultaForm form) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        this.minhasConsultasClienteServiceImpl.cancelarConsulta(token.getIdUsuario(), form);
        return ResponseEntity.ok().build();
    }
}
