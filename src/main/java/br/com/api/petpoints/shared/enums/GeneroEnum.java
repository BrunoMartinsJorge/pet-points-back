package br.com.api.petpoints.shared.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum GeneroEnum implements Serializable {
    M("MASCULINO"),
    F("FEMININO");
    private final String descricao;
    GeneroEnum(String descricao) {
        this.descricao = descricao;
    }
}
