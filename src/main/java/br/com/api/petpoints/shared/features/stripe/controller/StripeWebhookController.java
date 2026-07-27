package br.com.api.petpoints.shared.features.stripe.controller;

import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import br.com.api.petpoints.shared.features.payment.repository.CobrancaPixRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/pagamentos/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final CobrancaPixRepository cobrancaPixRepository;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/stripe")
    public ResponseEntity<String> receber(@RequestBody String payload,
                                          @RequestHeader("Stripe-Signature") String assinatura) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, assinatura, webhookSecret); // valida a assinatura
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Assinatura inválida");
        }

        if ("payment_intent.succeeded".equals(event.getType())) {
            event.getDataObjectDeserializer().getObject().ifPresent(obj -> {
                PaymentIntent intent = (PaymentIntent) obj;
                cobrancaPixRepository.findByGatewayId(intent.getId()).ifPresent(c -> {
                    if (c.getStatus() != StatusPagamentoEnum.APROVADO) {
                        c.setStatus(StatusPagamentoEnum.APROVADO);
                        c.setPagoEm(LocalDateTime.now());
                        cobrancaPixRepository.save(c);
                        // aqui sua regra de negócio: liberar consulta, notificar atendente...
                    }
                });
            });
        }
        return ResponseEntity.ok("ok");
    }
}
