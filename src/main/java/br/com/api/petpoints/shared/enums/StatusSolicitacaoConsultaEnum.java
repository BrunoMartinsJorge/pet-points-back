package br.com.api.petpoints.shared.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum StatusSolicitacaoConsultaEnum implements Serializable {
    PENDENTE("Pendente"),
    APROVADA("Aprovada"),
    REPROVADA("Reprovada"),
    CANCELADA("Cancelada");

    private final String descricao;
    StatusSolicitacaoConsultaEnum(String descricao) {
        this.descricao = descricao;
    }
}
