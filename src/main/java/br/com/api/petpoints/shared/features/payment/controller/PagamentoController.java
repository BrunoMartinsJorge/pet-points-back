package br.com.api.petpoints.shared.features.payment.controller;

import br.com.api.petpoints.shared.features.payment.dto.MercadoPagoDto;
import br.com.api.petpoints.shared.features.payment.dto.PagamentoDto;
import br.com.api.petpoints.shared.features.payment.service.MercadoPagoService;
import br.com.api.petpoints.shared.features.payment.service.PagamentoService;
import jakarta.persistence.EntityNotFoundException;
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

    /**
     * Endpoint que consulta (e atualiza) o status de um pagamento pelo id interno.
     *
     * @param id - Long - Id pagamento selecionado
     * @return - PagamentoDto.StatusPagamentoResponse - Status do pagamento
     */
    @GetMapping("/{id}/status")
    public ResponseEntity<PagamentoDto.StatusPagamentoResponse> consultarStatus(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.consultarStatus(id));
    }

    @GetMapping("/metodos")
    public ResponseEntity<List<MercadoPagoDto.PaymentMethodInfo>> listarMetodos(
            @RequestParam(defaultValue = "PIX") String marketplace) {
        return ResponseEntity.ok(mercadoPagoService.buscarMetodosPagamento(marketplace));
    }

    /**
     * Enpoint para Webhook do Mercado Pago que alerta sobre pagamentos efetuados
     *
     * @param notificacao - MercadoPagoDto.WebhookNotification - Informações do pagamento atualizado
     * @return Resposta de sucesso OK 200 ao finalizar normalmente a alteração
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
}
