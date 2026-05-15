package br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.dto.*;
import br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.forms.CancelarConsultaForm;
import br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.forms.SolicitacaoConsultaForm;
import br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.service.MinhasConsultasClienteServiceImpl;
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

    @GetMapping("/consultas-pendentes-confirmadas")
    public ResponseEntity<List<ConsultasPendentesConfirmadasDto>> listarConsultasConfirmadasPendentesOuIniciadasDoCliente(HttpServletRequest request) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok().body(this.minhasConsultasClienteServiceImpl.listarConsultasPendentes(token.getIdUsuario()));
    }

    @GetMapping
    public ResponseEntity<List<MinhasConsultasDto>> listarMinhasConsultas(HttpServletRequest request) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok(this.minhasConsultasClienteServiceImpl.listarMinhasConsultas(token.getIdUsuario()));
    }

    @GetMapping("/tipos-consulta")
    public ResponseEntity<List<TiposConsultaDto>> buscarTiposConsultaParaSolicitacao(){
        return ResponseEntity.ok().body(this.minhasConsultasClienteServiceImpl.listarTiposConsulta());
    }

    @GetMapping("/veterinarios-tipo-consulta/{idTipoConsulta}")
    public ResponseEntity<List<VeterinariosTipoConsultaDto>> buscarVeterinariosTipoConsultaParaSolicitacao(@PathVariable Long idTipoConsulta) {
        return ResponseEntity.ok().body(this.minhasConsultasClienteServiceImpl.listarVeterinariosTipoConsulta(idTipoConsulta));
    }

    @GetMapping("/horarios/{idVeterinario}")
    public ResponseEntity<List<DiaConsultasVeterinarioDto>> buscarHorariosParaSolicitacao(@PathVariable Long idVeterinario) {
        return ResponseEntity.ok().body(this.minhasConsultasClienteServiceImpl.buscarDiasHorariosDisponiveisVeterinario(idVeterinario));
    }

    @GetMapping("/pets")
    public ResponseEntity<List<OpcoesPetConsultasDto>> buscarPetsClienteConsulta(HttpServletRequest request) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok().body(this.minhasConsultasClienteServiceImpl.buscarPetsConsulta(token.getIdUsuario()));
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
