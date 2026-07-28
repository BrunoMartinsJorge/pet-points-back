package br.com.api.petpoints.shared.features.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonInclude(JsonInclude.Include.NON_NULL) // nao envia campos nulos
    public record OrderRequest(
            String type,                                                 // "online"
            @JsonProperty("external_reference") String externalReference,
            @JsonProperty("processing_mode") String processingMode,      // "automatic"
            @JsonProperty("total_amount") String totalAmount,            // "200.00" (string!)
            String description,
            Payer payer,
            Transactions transactions
    ) {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Payer(
                String email,
                @JsonProperty("first_name") String firstName,
                Identification identification
        ) {
        }
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Identification(
                String type,
                String number
        ) {
        }

        public record Transactions(List<Payment> payments) {
        }

        public record Payment(
                String amount,
                @JsonProperty("payment_method") PaymentMethod paymentMethod
        ) {
        }

        public record PaymentMethod(String id, String type) { // "pix" / "bank_transfer"
        }
    }

    // ---------- Resposta: POST /v1/orders e GET /v1/orders/{id} ----------
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OrderResponse(
            String id,                                                    // "ORDTST01..."
            String type,
            String status,                                                // processed / action_required / ...
            @JsonProperty("status_detail") String statusDetail,           // accredited / waiting_transfer / ...
            @JsonProperty("total_amount") String totalAmount,
            @JsonProperty("total_paid_amount") String totalPaidAmount,
            @JsonProperty("external_reference") String externalReference,
            String description,
            String currency,
            Transactions transactions
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Transactions(List<Payment> payments) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Payment(
                String id,                                                // "PAY01..."
                String amount,
                String status,
                @JsonProperty("status_detail") String statusDetail,
                @JsonProperty("date_of_expiration") String dateOfExpiration,
                @JsonProperty("payment_method") PaymentMethod paymentMethod
        ) {
        }

        // Para PIX, os dados do QR chegam aqui dentro.
        // OBS: confirme os nomes exatos na resposta real da sua conta.
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record PaymentMethod(
                String id,
                String type,
                @JsonProperty("qr_code") String qrCode,                   // "copia e cola" (string EMV)
                @JsonProperty("qr_code_base64") String qrCodeBase64,      // imagem PNG do QR em base64
                @JsonProperty("ticket_url") String ticketUrl             // pagina do MP com o QR
        ) {
        }
    }

    // ---------- Resposta: GET /v1/payment_methods/search ----------
    // OBS: se a resposta vier embrulhada em { "results": [...] }, crie um record
    // wrapper com List<PaymentMethodInfo> results e ajuste o service.
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PaymentMethodInfo(
            String id,
            String name,
            @JsonProperty("payment_type_id") String paymentTypeId,
            String status
    ) {
    }

    // ---------- Webhook (notificacao server-to-server) ----------
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WebhookNotification(
            String action,   // ex.: "payment.updated"
            String type,     // ex.: "payment" / "order"
            Data data
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Data(String id) {
        }
    }
}