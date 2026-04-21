package br.com.api.petpoints.modules.users.gerente.features.estoque.form;

import lombok.Getter;

@Getter
public class FiltrosProdutoForm {
    private String nome;
    private String tipoProduto;
    private Double precoMin;
    private Double precoMax;
}
