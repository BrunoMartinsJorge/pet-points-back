package br.com.api.petpoints.modules.users.gerente.features.movimentacoes.form;

import lombok.Getter;

@Getter
public class FiltroMovimentacoesForm {

    private String tipoMovimentacao;
    private Long lancadoPor;
    private Long produto;
}
