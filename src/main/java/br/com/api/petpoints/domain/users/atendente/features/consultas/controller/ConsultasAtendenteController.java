package br.com.api.petpoints.domain.users.atendente.features.consultas.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.domain.users.atendente.features.consultas.dto.AvaliacaoConsultaDto;
import br.com.api.petpoints.domain.users.atendente.features.consultas.dto.ConsultasAtendenteDto;
import br.com.api.petpoints.domain.users.atendente.features.consultas.dto.InformacoesPagamentoDto;
import br.com.api.petpoints.domain.users.atendente.features.consultas.dto.OpcaoClienteConsultaDto;
import br.com.api.petpoints.domain.users.atendente.features.consultas.dto.PendenciasFinanceirasClienteDto;
import br.com.api.petpoints.domain.users.atendente.features.consultas.forms.IndeferirConsultaForm;
import br.com.api.petpoints.domain.users.atendente.features.consultas.forms.RegistroConsultaAtendenteForm;
import br.com.api.petpoints.domain.users.atendente.features.consultas.service.ConsultasAtendenteServiceImpl;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto.DiaConsultasVeterinarioDto;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto.OpcoesPetConsultasDto;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto.TiposConsultaDto;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto.VeterinariosTipoConsultaDto;
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

    @GetMapping
    public ResponseEntity<List<ConsultasAtendenteDto>> listarConsultas() {
        return ResponseEntity.ok().body(this.consultasAtendenteService.listarConsultasPendentes());
    }

    @GetMapping("/historico")
    public ResponseEntity<List<ConsultasAtendenteDto>> listarHistoricoDeConsultas() {
        return ResponseEntity.ok().body(this.consultasAtendenteService.listarHistoricoDeConsultas());
    }

    @PutMapping("/aprovar/{idConsulta}")
    public ResponseEntity<?> aprovarSolicitacaoDeConsulta(HttpServletRequest request, @PathVariable Long idConsulta) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        this.consultasAtendenteService.deferirSolicitacaoDeConsulta(idConsulta, token.getIdUsuario());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/reprovar")
    public ResponseEntity<?> reprovarSolicitacaoDeConsulta(HttpServletRequest request, @RequestBody @Valid IndeferirConsultaForm form) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        this.consultasAtendenteService.indeferirSolicitacaoDeConsulta(form, token.getIdUsuario());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/listar-pagamentos-pendentes/{idCliente}")
    // @Operation - SWAGGER
    public ResponseEntity<List<ConsultasAtendenteDto>> listarConsultasComPagamentosPendentes(@PathVariable Long idCliente) {
        return ResponseEntity.ok().body(this.consultasAtendenteService.listarConsultasComPagamentosPendentesDoCliente(idCliente));
    }

    /**
     * Situação financeira do cliente (cobranças em aberto e atrasadas), consultada
     * pelo atendente antes de aprovar uma nova solicitação de consulta.
     */
    @GetMapping("/pendencias-financeiras/{idCliente}")
    public ResponseEntity<PendenciasFinanceirasClienteDto> buscarPendenciasFinanceirasDoCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok().body(this.consultasAtendenteService.buscarPendenciasFinanceirasDoCliente(idCliente));
    }

    @GetMapping("/pagamento/{idConsulta}")
    public ResponseEntity<InformacoesPagamentoDto> buscarPagamentoPorConsulta(@PathVariable Long idConsulta) {
        return ResponseEntity.ok().body(this.consultasAtendenteService.buscarInformacoesPagamento(idConsulta));
    }

    @GetMapping("/avaliacao/{idConsulta}")
    public ResponseEntity<AvaliacaoConsultaDto> buscarAvaliacaoPorConsulta(@PathVariable Long idConsulta) {
        return ResponseEntity.ok().body(this.consultasAtendenteService.buscarAvaliacao(idConsulta));
    }

    @GetMapping("/registro/clientes")
    public ResponseEntity<List<OpcaoClienteConsultaDto>> listarClientesParaRegistroDeConsulta() {
        return ResponseEntity.ok().body(this.consultasAtendenteService.listarClientesParaRegistro());
    }

    @GetMapping("/registro/pets/{idCliente}")
    public ResponseEntity<List<OpcoesPetConsultasDto>> listarPetsDoClienteParaRegistroDeConsulta(@PathVariable Long idCliente) {
        return ResponseEntity.ok().body(this.consultasAtendenteService.listarPetsDoCliente(idCliente));
    }

    @GetMapping("/registro/tipos-consulta")
    public ResponseEntity<List<TiposConsultaDto>> listarTiposConsultaParaRegistroDeConsulta() {
        return ResponseEntity.ok().body(this.consultasAtendenteService.listarTiposConsultaParaRegistro());
    }

    @GetMapping("/registro/veterinarios-tipo-consulta/{idTipoConsulta}")
    public ResponseEntity<List<VeterinariosTipoConsultaDto>> listarVeterinariosTipoConsultaParaRegistroDeConsulta(@PathVariable Long idTipoConsulta) {
        return ResponseEntity.ok().body(this.consultasAtendenteService.listarVeterinariosTipoConsulta(idTipoConsulta));
    }

    @GetMapping("/registro/horarios/{idVeterinario}")
    public ResponseEntity<List<DiaConsultasVeterinarioDto>> buscarHorariosVeterinarioParaRegistroDeConsulta(@PathVariable Long idVeterinario) {
        return ResponseEntity.ok().body(this.consultasAtendenteService.buscarHorariosVeterinario(idVeterinario));
    }

    @PostMapping("/registrar")
    public ResponseEntity<Void> registrarConsulta(HttpServletRequest request, @RequestBody @Valid RegistroConsultaAtendenteForm form) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        this.consultasAtendenteService.registrarConsulta(form, token.getIdUsuario());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{idConsulta}")
    public ResponseEntity<ConsultasAtendenteDto> buscarConsultaPorId(HttpServletRequest request, @PathVariable Long idConsulta) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok().body(this.consultasAtendenteService.buscarConsultaPorId(token.getIdUsuario(), idConsulta));
    }

}
