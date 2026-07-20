package br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto.*;
import br.com.api.petpoints.shared.form.AvaliacaoForm;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.forms.CancelarConsultaForm;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.forms.SolicitacaoConsultaForm;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.service.MinhasConsultasClienteServiceImpl;
import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/cliente/minhas-consultas")
@RequiredArgsConstructor
public class MinhasConsultasClienteController {

    private final MinhasConsultasClienteServiceImpl minhasConsultasClienteServiceImpl;

    private Long idUsuario(HttpServletRequest request) {
        return new TokenModel(request.getHeader("Authorization")).getIdUsuario();
    }

    @GetMapping("/cards")
    public ResponseEntity<InformacoesCardsConsultasClienteDto> gerarInformacoesCardsConsultasCliente(HttpServletRequest request) {
        return ResponseEntity.ok().body(this.minhasConsultasClienteServiceImpl.gerarInformacoesCards(this.idUsuario(request)));
    }

    @GetMapping("/consultas-confirmadas")
    public ResponseEntity<List<MinhasConsultasDto>> listarConsultasConfirmadasOuIniciadasDoCliente(HttpServletRequest request) {
        return ResponseEntity.ok().body(this.minhasConsultasClienteServiceImpl.listarConsultasAprovadas(this.idUsuario(request)));
    }

    @GetMapping("/consultas-pendentes")
    public ResponseEntity<List<MinhasConsultasDto>> listarConsultasPendentesDoCliente(HttpServletRequest request) {
        return ResponseEntity.ok().body(this.minhasConsultasClienteServiceImpl.listarConsultasPendentes(this.idUsuario(request)));
    }

    @GetMapping("/proxima-consulta")
    public ResponseEntity<MinhasConsultasDto> buscarProximaConsultaCliente(HttpServletRequest request) {
        return ResponseEntity.ok().body(this.minhasConsultasClienteServiceImpl.buscarProximaConsulta(this.idUsuario(request)));
    }

    @GetMapping("/consulta-atual")
    public ResponseEntity<MinhasConsultasDto> buscarConsultaAtualCliente(HttpServletRequest request) {
        return ResponseEntity.ok().body(this.minhasConsultasClienteServiceImpl.buscarConsultaAtual(this.idUsuario(request)));
    }

    @GetMapping
    public ResponseEntity<List<MinhasConsultasDto>> listarMinhasConsultas(HttpServletRequest request) {
        return ResponseEntity.ok(this.minhasConsultasClienteServiceImpl.listarMinhasConsultas(this.idUsuario(request)));
    }

    @GetMapping("/detalhes/{idConsulta}")
    public ResponseEntity<DetalhesConsultaSelecionadaDto> buscarDetalhesConsulta(@PathVariable Long idConsulta) {
        return ResponseEntity.ok().body(this.minhasConsultasClienteServiceImpl.buscarDetalhesConsulta(idConsulta));
    }

    @GetMapping("/tipos-consulta")
    public ResponseEntity<List<TiposConsultaDto>> buscarTiposConsultaParaSolicitacao() {
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
        return ResponseEntity.ok().body(this.minhasConsultasClienteServiceImpl.buscarPetsConsulta(this.idUsuario(request)));
    }

    @PostMapping("/solicitar")
    public ResponseEntity<Void> solicitarMinhasConsulta(HttpServletRequest request, @RequestBody @Valid SolicitacaoConsultaForm form) {
        this.minhasConsultasClienteServiceImpl.solicitarNovaConsulta(this.idUsuario(request), form);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pagamento/{idConsulta}")
    public ResponseEntity<PagamentoDto> buscarPagamentoPorConsulta(@PathVariable Long idConsulta) {
        return ResponseEntity.ok().body(this.minhasConsultasClienteServiceImpl.buscarPagamentoConsulta(idConsulta));
    }

    @PutMapping("/cancelar-consulta")
    public ResponseEntity<?> cancelarConsulta(HttpServletRequest request, @RequestBody @Valid CancelarConsultaForm form) {
        this.minhasConsultasClienteServiceImpl.cancelarConsulta(this.idUsuario(request), form);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{idConsulta}")
    public ResponseEntity<MinhasConsultasDto> buscarConsultaPorId(@PathVariable Long idConsulta) {
        return ResponseEntity.ok().body(this.minhasConsultasClienteServiceImpl.buscarConsultaPorId(idConsulta));
    }

    @PostMapping("/registrar-comprovante/{idConsulta}")
    public ResponseEntity<Void> registrarComprovanteConsulta(HttpServletRequest request, @PathVariable Long idConsulta, @RequestParam MultipartFile arquivo) {
        this.minhasConsultasClienteServiceImpl.registrarComprovante(idConsulta, this.idUsuario(request), arquivo);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/alterar-forma-pagamento/{idConsulta}/{formaPagamento}")
    public ResponseEntity<Void> alterarFormaPagamento(@PathVariable Long idConsulta, @PathVariable TipoPagamentoEnum formaPagamento) {
        this.minhasConsultasClienteServiceImpl.alterarFormaPagamentoConsulta(idConsulta, formaPagamento);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/avaliacao-consulta/{idConsulta}")
    public ResponseEntity<AvaliacaoConsultaDto> buscarAvaliacaoPorConsulta(HttpServletRequest request, @PathVariable Long idConsulta) {
        return ResponseEntity.ok().body(this.minhasConsultasClienteServiceImpl.buscarAvaliacaoPorConsulta(this.idUsuario(request), idConsulta));
    }

    @PostMapping("/avaliar-consulta/{idConsulta}")
    public ResponseEntity<Void> enviarAvaliacaoConsulta(HttpServletRequest request, @PathVariable Long idConsulta, @RequestBody AvaliacaoForm form) {
        this.minhasConsultasClienteServiceImpl.avaliarConsulta(this.idUsuario(request), idConsulta, form);
        return ResponseEntity.ok().build();
    }
}
