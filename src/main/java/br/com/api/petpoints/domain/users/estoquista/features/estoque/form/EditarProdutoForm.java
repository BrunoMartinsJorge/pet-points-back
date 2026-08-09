package br.com.api.petpoints.domain.users.estoquista.features.estoque.form;

import br.com.api.petpoints.shared.enums.TipoProdutoEnum;
import lombok.Getter;

@Getter
public class EditarProdutoForm {

    private String nome;
    private String descricao;
    private TipoProdutoEnum tipo;
    private int quantidadeAbaixoEstoque;
    private double valorUnitario;
}
