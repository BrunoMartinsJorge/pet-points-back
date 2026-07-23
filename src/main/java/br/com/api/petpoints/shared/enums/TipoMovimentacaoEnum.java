package br.com.api.petpoints.shared.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum TipoMovimentacaoEnum implements Serializable {
    ENTRADA("Entrada"),
    SAIDA("Saída");

    private final String descricao;
    TipoMovimentacaoEnum(String descricao) {
        this.descricao = descricao;
    }
}
