package br.com.api.petpoints.domain.users.gerente.features.financeiro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResumoFinanceiroDto {

    private double receitaTotal;
    private double ticketMedio;
    private double valorAReceber;
    private double valorEmAtraso;
    private double valorRecusado;
    private int quantidadeTransacoes;
    private int quantidadeAprovados;
    private Double variacaoPercentualReceita;
}
