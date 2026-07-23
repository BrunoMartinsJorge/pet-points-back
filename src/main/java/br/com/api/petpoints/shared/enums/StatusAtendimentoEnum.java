package br.com.api.petpoints.shared.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum StatusAtendimentoEnum implements Serializable {
    PENDENTE("Pendente"),
    EM_ANDAMENTO("Em Andamento"),
    FINALIZADO("Finalizado");

    private final String descricao;
    StatusAtendimentoEnum(String descricao) {
        this.descricao = descricao;
    }
}
