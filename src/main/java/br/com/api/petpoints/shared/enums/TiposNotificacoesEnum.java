package br.com.api.petpoints.shared.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum TiposNotificacoesEnum implements Serializable {
    CONSULTA("Consulta"),
    ATENDIMENTO("Atendimento"),
    MENSAGEM("Mensagem"),
    ALERTA("Alerta");
    private final String descricao;
    TiposNotificacoesEnum(String descricao) {
        this.descricao = descricao;
    }
}
