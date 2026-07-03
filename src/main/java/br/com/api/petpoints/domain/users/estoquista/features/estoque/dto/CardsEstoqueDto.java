package br.com.api.petpoints.domain.users.estoquista.features.estoque.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardsEstoqueDto {
    private double valorTotalEstoque;
    private int quantidadeProdutosEstoque;
    private int produtosAbaixoEstoque;
}
