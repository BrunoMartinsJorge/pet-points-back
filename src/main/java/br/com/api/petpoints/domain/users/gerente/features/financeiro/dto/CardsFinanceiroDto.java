package br.com.api.petpoints.domain.users.gerente.features.financeiro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardsFinanceiroDto {

    private double receitaMesAtual;
    private Double variacaoReceitaMesAtual;
    private double receitaHoje;
    private Double variacaoReceitaHoje;
    private double valorPendente;
    private int quantidadePendentes;
    private double valorEmAtraso;
    private int quantidadeEmAtraso;
}
