package br.com.api.petpoints.shared.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum StatusConsultaEnum implements Serializable {
    PENDENTE("Pendente"),
    APROVADA("Aprovada"),
    REPROVADA("Reprovada"),
    INICIADO("Iniciado"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusConsultaEnum(String descricao) {
        this.descricao = descricao;
    }
}
