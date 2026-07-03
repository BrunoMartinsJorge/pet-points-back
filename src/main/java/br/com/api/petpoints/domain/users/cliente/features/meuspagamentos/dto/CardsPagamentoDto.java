package br.com.api.petpoints.domain.users.cliente.features.meuspagamentos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardsPagamentoDto {

    private int pagamentosEfetuados;
    private int pagamentosAtrasados;
    private int pagamentosReprovados;
}
