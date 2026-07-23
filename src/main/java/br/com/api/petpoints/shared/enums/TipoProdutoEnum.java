package br.com.api.petpoints.shared.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum TipoProdutoEnum implements Serializable {
    RACAO("Ração"),
    BRINQUEDO("Brinquedo"),
    COSMETICO("Cosmético"),
    ROUPA("Roupa"),
    HIGIENE("Higiene");

    private final String descricao;
    TipoProdutoEnum(String descricao) {
        this.descricao = descricao;
    }
}
