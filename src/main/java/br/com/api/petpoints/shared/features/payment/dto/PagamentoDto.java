package br.com.api.petpoints.shared.features.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public final class PagamentoDto {

    private PagamentoDto() {
    }

    public record CriarPagamentoPixForm(
            @NotNull @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
            BigDecimal valor,

            @NotBlank
            String descricao,

            @NotBlank @Email
            String emailPagador,


            String nomePagador,


            String externalReference,

            String cpf
    ) {
    }

    public record PagamentoPixResponse(
            Long pagamentoId,
            String orderId,
            String status,
            String valor,
            String qrCode,
            String qrCodeBase64,
            String ticketUrl
    ) {
    }

    public record StatusPagamentoResponse(
            Long pagamentoId,
            String orderId,
            String status,
            String statusMercadoPago,
            String statusDetalhe
    ) {
    }
}