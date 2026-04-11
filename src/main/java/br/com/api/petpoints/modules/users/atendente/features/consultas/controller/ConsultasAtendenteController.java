package br.com.api.petpoints.modules.users.atendente.features.consultas.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.modules.users.atendente.features.consultas.dto.ConsultasDto;
import br.com.api.petpoints.modules.users.atendente.features.consultas.forms.DeferirIndeferirSolicitacaoConsultaForm;
import br.com.api.petpoints.modules.users.atendente.features.consultas.service.ConsultasAtendenteServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/atendente/consultas")
@RequiredArgsConstructor
public class ConsultasAtendenteController {

    private final ConsultasAtendenteServiceImpl consultasAtendenteService;

    @PutMapping("/aprovar-solicitacao")
    public ResponseEntity<?> aprovarSolicitacaoDeConsulta(HttpServletRequest request, @RequestBody @Valid DeferirIndeferirSolicitacaoConsultaForm form) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        this.consultasAtendenteService.deferirSolicitacaoDeConsulta(form, token.getIdUsuario());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/reprovar-solicitacao")
    public ResponseEntity<?> reprovarSolicitacaoDeConsulta(HttpServletRequest request, @RequestBody @Valid DeferirIndeferirSolicitacaoConsultaForm form) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        this.consultasAtendenteService.indeferirSolicitacaoDeConsulta(form, token.getIdUsuario());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/listar-pagamentos-pendentes/{idCliente}")
    public ResponseEntity<List<ConsultasDto>> listarConsultasComPagamentosPendentes(@PathVariable Long idCliente) {
        return ResponseEntity.ok().body(this.consultasAtendenteService.listarConsultasComPagamentosPendentesDoCliente(idCliente));
    }

    @GetMapping
    public ResponseEntity<List<ConsultasDto>> listarConsultas() {
        return ResponseEntity.ok().body(this.consultasAtendenteService.listarConsultasPendentes());
    }
}
