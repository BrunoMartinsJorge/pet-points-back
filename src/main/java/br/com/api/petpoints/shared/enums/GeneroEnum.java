package br.com.api.petpoints.shared.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum GeneroEnum implements Serializable {

    M("Masculino"),
    F("Feminino");

    private final String descricao;
    GeneroEnum(String descricao) {
        this.descricao = descricao;
    }
}
