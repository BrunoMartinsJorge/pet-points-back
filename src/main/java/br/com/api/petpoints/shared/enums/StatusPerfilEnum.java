package br.com.api.petpoints.shared.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum StatusPerfilEnum implements Serializable {
    A("ATIVO"),
    D("DESATIVADO");
    private final String descricao;
    StatusPerfilEnum(String descricao) {
        this.descricao = descricao;
    }
}
