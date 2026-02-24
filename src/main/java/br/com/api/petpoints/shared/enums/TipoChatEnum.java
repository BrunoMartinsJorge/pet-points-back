package br.com.api.petpoints.shared.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum TipoChatEnum implements Serializable {
    ATENDIMENTO("Atendimento"),
    INTERNO("Interno");

    private final String descricao;
    TipoChatEnum(String descricao) {
        this.descricao = descricao;
    }
}
