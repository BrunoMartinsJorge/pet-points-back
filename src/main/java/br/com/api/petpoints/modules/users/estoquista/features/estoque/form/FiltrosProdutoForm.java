package br.com.api.petpoints.modules.users.estoquista.features.estoque.form;

import br.com.api.petpoints.shared.enums.TipoProdutoEnum;
import lombok.Getter;

@Getter
public class FiltrosProdutoForm {

    private String nome;
    private boolean quantidadeAbaixoEstoque;
    private TipoProdutoEnum tipoProduto;
    private double precoMin;
    private double precoMax;
}
