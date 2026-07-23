package br.com.api.petpoints.shared.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum StatusPagamentoEnum implements Serializable {
    ENVIADO("Enviado"),
    PENDENTE("Pendente"),
    APROVADO("Aprovado"),
    REPROVADO("Reprovado");

    private final String descricao;
    StatusPagamentoEnum(String descricao) {
        this.descricao = descricao;
    }
}
