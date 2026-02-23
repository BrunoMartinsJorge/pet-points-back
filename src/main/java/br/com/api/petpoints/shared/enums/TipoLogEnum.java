package br.com.api.petpoints.shared.enums;

import java.io.Serializable;

public enum TipoLogEnum implements Serializable {
    LOGIN,
    REGISTRO,
    ERRO,
    MOVIMENTACAO_ENTRADA,
    MOVIMENTACAO_SAIDA,
    DESATIVOU_CONTA,
    CANCELOU_CONSULTA,
    SOLICITOU_CONSULTA,
    DEFERIU_CONSULTA,
    INDEFERIU_CONSULTA;
}
