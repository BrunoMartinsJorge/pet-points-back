package br.com.api.petpoints.shared.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum TipoPagamentoEnum implements Serializable {
    PIX("PIX"),
    CARTAO("Cartão"),
    DINHEIRO("Dinheiro");

    private final String descricao;
    TipoPagamentoEnum(String descricao) {
        this.descricao = descricao;
    }
}
