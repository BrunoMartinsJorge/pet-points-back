package br.com.api.petpoints.shared.features.payment.service;

import br.com.api.petpoints.core.api.StripeProperties;
import com.stripe.StripeClient;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeService {

    private final StripeClient stripeClient;
    private final StripeProperties properties;

    public PaymentIntent criarPagamentoIntent(long amountCents, String currency, String descricao, String externalReference) {
        try {
            PaymentIntentCreateParams.Builder params = PaymentIntentCreateParams.builder().setAmount(amountCents).setCurrency(currency).setDescription(descricao).setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()
            );
            if (externalReference != null)
                params.putMetadata("external_reference", externalReference);
            RequestOptions options = RequestOptions.builder().setIdempotencyKey(UUID.randomUUID().toString()).build();
            return stripeClient.paymentIntents().create(params.build(), options);
        } catch (StripeException e) {
            log.error("[ERRO DE PAGAMENTO] - Erro ao criar pagamento intent pela Stripe.", e);
            throw new StripeIntegracaoException("Erro ao criar pagamento na Stripe: " + e.getMessage(), e);
        }
    }

    public Session criarCheckoutSession(long amountCents, String currency, String descricao,
                                        String clientReferenceId, String emailCliente) {
        try {
            SessionCreateParams.Builder params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(properties.getSuccessUrl())
                    .setCancelUrl(properties.getCancelUrl())
                    .setClientReferenceId(clientReferenceId)          // id interno do pagamento
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency(currency)         // "brl"
                                                    .setUnitAmount(amountCents)    // centavos
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(descricao)
                                                                    .build())
                                                    .build())
                                    .build())
                    .putMetadata("pagamento_id", clientReferenceId);

            if (emailCliente != null) {
                params.setCustomerEmail(emailCliente);
            }

            RequestOptions options = RequestOptions.builder()
                    .setIdempotencyKey(UUID.randomUUID().toString())
                    .build();

            return stripeClient.checkout().sessions().create(params.build(), options);
        } catch (StripeException e) {
            log.error("Erro ao criar Checkout Session na Stripe.", e);
            throw new StripeIntegracaoException("Erro ao criar sessão de checkout: " + e.getMessage(), e);
        }
    }

    public Session buscarCheckoutSession(String sessionId) {
        try {
            return stripeClient.checkout().sessions().retrieve(sessionId);
        } catch (StripeException e) {
            throw new StripeIntegracaoException("Erro ao buscar sessão " + sessionId + ": " + e.getMessage(), e);
        }
    }

    public PaymentIntent buscarPaymentIntent(String id) {
        try {
            return stripeClient.paymentIntents().retrieve(id);
        } catch (StripeException e) {
            throw new StripeIntegracaoException("Erro ao buscar PaymentIntent " + id + ": " + e.getMessage(), e);
        }
    }

    /** Valida a assinatura e devolve o evento já desserializado. Uma linha, feita certo. */
    public Event validarExtrairEvento(String payloadCru, String sigHeader) {
        try {
            return Webhook.constructEvent(payloadCru, sigHeader, properties.getWebhookSecret());
        } catch (SignatureVerificationException e) {
            log.warn("Assinatura de webhook Stripe inválida.");
            log.error(e.getMessage());
            log.error(e.getStripeError().getMessage());

            throw new StripeIntegracaoException("Assinatura de webhook inválida.", e);
        }
    }

    public static class StripeIntegracaoException extends RuntimeException {
        public StripeIntegracaoException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
