package br.com.api.petpoints.domain.users.gerente.features.financeiro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReceitaPorTipoPagamentoDto {

    private String tipoPagamento;
    private double valor;
    private double percentual;
}
