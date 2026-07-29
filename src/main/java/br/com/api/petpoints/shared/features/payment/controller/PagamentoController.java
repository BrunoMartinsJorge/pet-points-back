package br.com.api.petpoints.shared.features.payment.controller;

import br.com.api.petpoints.shared.features.payment.dto.MercadoPagoDto;
import br.com.api.petpoints.shared.features.payment.dto.PagamentoDto;
import br.com.api.petpoints.shared.features.payment.service.MercadoPagoService;
import br.com.api.petpoints.shared.features.payment.service.PagamentoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;
    private final MercadoPagoService mercadoPagoService;

    public PagamentoController(PagamentoService pagamentoService,
                               MercadoPagoService mercadoPagoService) {
        this.pagamentoService = pagamentoService;
        this.mercadoPagoService = mercadoPagoService;
    }

    /** Cria um pagamento PIX e devolve o QR Code para exibir ao cliente. */
    @PostMapping("/pix")
    public ResponseEntity<PagamentoDto.PagamentoPixResponse> criarPix(
            @RequestBody @Valid PagamentoDto.CriarPagamentoPixForm form) {
        PagamentoDto.PagamentoPixResponse resposta = pagamentoService.criarPagamentoPix(form);
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
        // 502: o problema veio de um serviço externo (o MP), não do cliente.
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("erro", e.getMessage()));
    }
}
