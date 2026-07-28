package br.com.api.petpoints.shared.features.payment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

/**
 * DTOs que espelham o JSON da API do Mercado Pago (Orders API).
 *
 * Cada record leva @JsonNaming(SnakeCaseStrategy) para converter camelCase (Java)
 * <-> snake_case (JSON do MP). Como o MP devolve muitos campos extras nas respostas,
 * deixe a flag abaixo no seu application.properties para não quebrar na desserializacao:
 *
 *     spring.jackson.deserialization.fail-on-unknown-properties=false
 *
 * (ou anote cada record de resposta com @JsonIgnoreProperties(ignoreUnknown = true)).
 */
public final class MercadoPagoDto {

    private MercadoPagoDto() {
    }

    // ---------- Requisicao: POST /v1/orders ----------
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL) // nao envia campos nulos
    public record OrderRequest(
            String type,                 // "online"
            String externalReference,    // sua referencia interna
            String processingMode,       // "automatic"
            String totalAmount,          // "200.00" (string!)
            String description,
            Payer payer,
            Transactions transactions
    ) {
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Payer(String email, String firstName) {
        }

        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public record Transactions(List<Payment> payments) {
        }

        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public record Payment(String amount, PaymentMethod paymentMethod) {
        }

        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public record PaymentMethod(String id, String type) { // "pix" / "bank_transfer"
        }
    }

    // ---------- Resposta: POST /v1/orders e GET /v1/orders/{id} ----------
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record OrderResponse(
            String id,                   // "ORDTST01..."
            String type,
            String status,               // created / action_required / processed / canceled / refunded ...
            String statusDetail,         // waiting_transfer / accredited / ...
            String totalAmount,
            String totalPaidAmount,
            String externalReference,
            String description,
            String currency,
            Transactions transactions
    ) {
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public record Transactions(List<Payment> payments) {
        }

        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public record Payment(
                String id,               // "PAY01..."
                String amount,
                String status,
                String statusDetail,
                PaymentMethod paymentMethod
        ) {
        }

        // Para PIX, os dados do QR chegam aqui dentro.
        // OBS: confirme os nomes exatos (qr_code / qr_code_base64 / ticket_url) na resposta real da sua conta.
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public record PaymentMethod(
                String id,
                String type,
                String qrCode,           // "copia e cola" (string EMV)
                String qrCodeBase64,     // imagem PNG do QR em base64
                String ticketUrl         // pagina do MP com o QR
        ) {
        }
    }

    // ---------- Resposta: GET /v1/payment_methods/search ----------
    // OBS: se a resposta vier embrulhada em { "results": [...] }, crie um record
    // wrapper com List<PaymentMethodInfo> results e ajuste o service.
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record PaymentMethodInfo(
            String id,
            String name,
            String paymentTypeId,
            String status
    ) {
    }

    // ---------- Webhook (notificacao server-to-server) ----------
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record WebhookNotification(
            String action,   // ex.: "payment.updated"
            String type,     // ex.: "payment" / "order"
            Data data
    ) {
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public record Data(String id) {
        }
    }
}