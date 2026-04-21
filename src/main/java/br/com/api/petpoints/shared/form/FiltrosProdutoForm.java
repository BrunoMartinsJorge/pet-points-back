package br.com.api.petpoints.shared.form;

import jakarta.annotation.Nullable;
import lombok.Getter;

@Getter
public class FiltrosProdutoForm {

    private String nome;
    private boolean todosOsProdutos;
    private String tipoProduto;
    @Nullable
    private Double precoMin;
    @Nullable
    private Double precoMax;
}
