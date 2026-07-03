package br.com.api.petpoints.domain.users.estoquista.features.estoque.form;

import br.com.api.petpoints.shared.enums.TipoProdutoEnum;
import lombok.Getter;

@Getter
public class NovoProdutoForm {

    private String nome;
    private String descricao;
    private TipoProdutoEnum tipo;
    private int quantidade;
    private int quantidadeMinima;
    private double valorUnitario;
}
