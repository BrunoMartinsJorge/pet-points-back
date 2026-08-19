package br.com.api.petpoints.shared.features.payment.forms;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CriarPagamentoCartaoStripeForm {

    @NotNull @DecimalMin(value = "0.01", message = "O valor de cobrança deve ser maior que zero!")
    private BigDecimal valor;

    @NotBlank
    private String descricao;

    private String externalReference;
}
