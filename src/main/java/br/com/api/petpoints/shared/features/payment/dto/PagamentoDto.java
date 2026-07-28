package br.com.api.petpoints.shared.features.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTOs da SUA API (o que o frontend envia e recebe).
 * As validações (@NotNull etc.) exigem a dependência spring-boot-starter-validation.
 */
public final class PagamentoDto {

    private PagamentoDto() {
    }

    /** O que o front envia para gerar um pagamento PIX. */
    public record CriarPagamentoPixForm(
            @NotNull @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
            BigDecimal valor,

            @NotBlank
            String descricao,

            @NotBlank @Email
            String emailPagador,

            // Em sandbox, use "APRO" para forçar aprovação. Em produção, o nome real.
            String nomePagador,

            // Sua referência interna (ex.: id da consulta). Opcional.
            String externalReference,

            String cpf
    ) {
    }

    /** O que devolvemos ao front logo após criar o PIX (com o QR pronto para exibir). */
    public record PagamentoPixResponse(
            Long pagamentoId,        // id do PagamentoModel no seu banco
            String orderId,          // id da order no Mercado Pago ("ORD...")
            String status,           // seu StatusPagamentoEnum
            String valor,
            String qrCode,           // "copia e cola"
            String qrCodeBase64,     // <img src="data:image/png;base64,...">
            String ticketUrl        // alternativa: redirecionar para a página do MP
    ) {
    }

    /** Resposta da consulta de status. */
    public record StatusPagamentoResponse(
            Long pagamentoId,
            String orderId,
            String status,                 // seu enum já mapeado
            String statusMercadoPago,      // status cru do MP
            String statusDetalhe           // status_detail do MP
    ) {
    }
}