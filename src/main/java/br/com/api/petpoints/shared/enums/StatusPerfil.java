package br.com.api.petpoints.shared.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum StatusPerfil implements Serializable {
    A("ATIVO"),
    D("DESATIVADO");
    private final String descricao;
    StatusPerfil(String descricao) {
        this.descricao = descricao;
    }
}
