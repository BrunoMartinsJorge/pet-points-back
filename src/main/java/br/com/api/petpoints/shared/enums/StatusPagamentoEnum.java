package br.com.api.petpoints.shared.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum StatusPagamentoEnum implements Serializable {
    ENVIADO("Enviado"),
    PENDENTE("Pendente"),
    APROVADO("Aprovado"),
    REPROVADO("Reprovado"),
    CANCELADO("Cancelado"),
    DEVOLVIDO("Devolvido"),
    RECUSADO("Recusado");

    private final String descricao;
    StatusPagamentoEnum(String descricao) {
        this.descricao = descricao;
    }
}
