package br.com.api.petpoints.shared.features.payment.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.shared.features.payment.dto.MercadoPagoDto;
import br.com.api.petpoints.shared.features.payment.dto.PagamentoDto;
import br.com.api.petpoints.shared.features.payment.forms.CriarPagamentoCartaoStripeForm;
import br.com.api.petpoints.shared.features.payment.service.MercadoPagoService;
import br.com.api.petpoints.shared.features.payment.service.PagamentoService;
import br.com.api.petpoints.shared.features.payment.service.StripeService;
import br.com.api.petpoints.shared.models.PagamentoModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import com.stripe.model.PaymentIntent;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoService pagamentoService;
    private final MercadoPagoService mercadoPagoService;
    private final UsuarioRepository usuarioRepository;

    /** Cria um pagamento PIX avulso e devolve o QR Code para exibir ao cliente. */
    @PostMapping("/pix")
    public ResponseEntity<PagamentoDto.PagamentoPixResponse> criarPix(
            HttpServletRequest request,
            @RequestBody @Valid PagamentoDto.CriarPagamentoPixForm form) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        UsuarioModel usuario = usuarioRepository.findById(token.getIdUsuario())
                .orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: " + token.getIdUsuario() + " não encontrado!"));
        PagamentoDto.PagamentoPixResponse resposta = pagamentoService.gerarCobrancaPix(new PagamentoModel(), form, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    /** Consulta (e atualiza) o status de um pagamento pelo id interno. */
    @GetMapping("/{id}/status")
    public ResponseEntity<PagamentoDto.StatusPagamentoResponse> consultarStatus(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.consultarStatus(id));
    }

    /** Lista os métodos de pagamento disponíveis no Mercado Pago. */
    @GetMapping("/metodos")
    public ResponseEntity<List<MercadoPagoDto.PaymentMethodInfo>> listarMetodos(
            @RequestParam(defaultValue = "PIX") String marketplace) {
        return ResponseEntity.ok(mercadoPagoService.buscarMetodosPagamento(marketplace));
    }

    /**
     * Endpoint que o Mercado Pago chama quando o status muda (webhook).
     * Deve responder 200 rápido; a lógica pesada, se crescer, deveria ser assíncrona.
     * Cadastre esta URL no painel do MP OU envie "notification_url" na criação da order.
     */
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody(required = false) MercadoPagoDto.WebhookNotification notificacao) {
        if (notificacao != null && notificacao.data() != null && notificacao.data().id() != null) {
            pagamentoService.processarWebhook(notificacao.data().id());
        }
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> naoEncontrado(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
    }

    @ExceptionHandler(MercadoPagoService.MercadoPagoException.class)
    public ResponseEntity<Map<String, String>> erroMercadoPago(MercadoPagoService.MercadoPagoException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("erro", e.getMessage()));
    }

    @PostMapping("/stripe/intent")
    public ResponseEntity<Map<String, String>> criarIntent(
            HttpServletRequest request,
            @RequestBody @Valid CriarPagamentoCartaoStripeForm form) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        UsuarioModel usuario = usuarioRepository.findById(token.getIdUsuario())
                .orElseThrow(() -> new UsuarioNaoEncontrado("Usuário não encontrado!"));
        PaymentIntent pi = pagamentoService.iniciarPagamentoCartaoStripe(new PagamentoModel(), form, usuario);
        return ResponseEntity.ok(Map.of("clientSecret", pi.getClientSecret(), "paymentIntentId", pi.getId()));
    }

    /** Stripe chama aqui. Precisa do corpo CRU (String) pra assinatura bater. */
    @PostMapping("/stripe/webhook")
    public ResponseEntity<Void> webhookStripe(@RequestBody String payload,
                                              @RequestHeader("Stripe-Signature") String assinatura) {
        try {
            pagamentoService.processarWebhookStripe(payload, assinatura);
            return ResponseEntity.ok().build();
        } catch (StripeService.StripeIntegracaoException e) {
            return ResponseEntity.badRequest().build(); // assinatura inválida -> 400, Stripe não reprocessa
        }
    }
}
