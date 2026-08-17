package br.com.api.petpoints.domain.users.cliente.features.meuspagamentos.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.domain.users.cliente.features.meuspagamentos.dto.CardsPagamentoDto;
import br.com.api.petpoints.domain.users.cliente.features.meuspagamentos.dto.DetalhesPagamentoDto;
import br.com.api.petpoints.domain.users.cliente.features.meuspagamentos.dto.PagamentosDto;
import br.com.api.petpoints.domain.users.cliente.features.meuspagamentos.service.MeusPagamentosServiceImpl;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto.MinhasConsultasDto;
import br.com.api.petpoints.domain.users.cliente.shared.dto.PagamentoConsultaDto;
import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import br.com.api.petpoints.shared.features.payment.dto.PagamentoDto;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cliente/pagamentos")
@RequiredArgsConstructor
public class MeusPagamentosController {

    private final MeusPagamentosServiceImpl meusPagamentosService;

    @GetMapping("/informacoes-cards")
    public ResponseEntity<CardsPagamentoDto> buscarInformacoesCardsPagamentosCliente(HttpServletRequest request) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok().body(this.meusPagamentosService.buscarInformacoesCards(token.getIdUsuario()));
    }

    @GetMapping("/{idPagamento}")
    public ResponseEntity<PagamentoConsultaDto> buscarPagamentoPorId(@PathVariable Long idPagamento) {
        return ResponseEntity.ok().body(this.meusPagamentosService.buscarPagamentoPorId(idPagamento));
    }

    @GetMapping("/pendentes-atrasados")
    public ResponseEntity<List<PagamentosDto>> listarPagamentosPendentesAtrasadosPorCliente(HttpServletRequest request) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok().body(this.meusPagamentosService.listarPagamentosPendentesAtrasados(token.getIdUsuario()));
    }

    @GetMapping("/reprovados")
    public ResponseEntity<List<PagamentosDto>> listarPagamentosReprovadosPorCliente(HttpServletRequest request) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok().body(this.meusPagamentosService.listarPagamentosReprovados(token.getIdUsuario()));
    }

    @GetMapping("/historicos")
    public ResponseEntity<List<PagamentosDto>> listarHistoricoPagamentosPorCliente(HttpServletRequest request) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok().body(this.meusPagamentosService.listarHistoricoPagamentos(token.getIdUsuario()));
    }

    /** Cliente clica "Efetuar Pagamento" -> gera a Checkout Session e devolve a URL. */
    @PostMapping("/{idPagamento}/checkout")
    public ResponseEntity<Map<String, String>> iniciarCheckout(
            HttpServletRequest request,
            @PathVariable Long idPagamento) {
        return ResponseEntity.ok().body(this.meusPagamentosService.iniciarCheckout(TokenUtils.getIdUsuario(request), idPagamento));
    }

    @GetMapping("/checkout/status")
    public ResponseEntity<PagamentoDto.StatusPagamentoResponse> statusPorSessao(
            @RequestParam String sessionId) {
        return ResponseEntity.ok(this.meusPagamentosService.sincronizarCheckoutPorSessao(sessionId));
    }

    @GetMapping("/consulta-pagamento/{idConsulta}")
    public ResponseEntity<MinhasConsultasDto> buscarDetalhesConsultaPagamento(@PathVariable Long idConsulta) {
        return ResponseEntity.ok().body(this.meusPagamentosService.buscarInformacoesConsultaPagamento(idConsulta));
    }

    @PutMapping("/alterar-forma-pagamento/{idPagamento}/{novaForm}")
    public ResponseEntity<Void> alterarFormaPagamento(@PathVariable Long idPagamento, @PathVariable TipoPagamentoEnum novaForm) {
        this.meusPagamentosService.alterarFormaPagamento(idPagamento, novaForm);
        return ResponseEntity.noContent().build();
    }
}
