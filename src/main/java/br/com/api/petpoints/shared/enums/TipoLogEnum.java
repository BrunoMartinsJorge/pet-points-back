package br.com.api.petpoints.shared.enums;

import java.io.Serializable;

public enum TipoLogEnum implements Serializable {

    LOGIN,
    REGISTRO,
    ERRO,
    MOVIMENTACAO_ENTRADA,
    MOVIMENTACAO_SAIDA,
    SE_DESATIVOU,
    CANCELOU_CONSULTA,
    SOLICITOU_CONSULTA,
    DEFERIU_CONSULTA,
    CONSULTA_INICIADA,
    CONSULTA_FINALIZADA,
    DESATIVOU_PERFIL,
    INDEFERIU_CONSULTA;
}
