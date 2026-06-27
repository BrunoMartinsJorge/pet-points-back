package br.com.api.petpoints.modules.users.atendente.features.consultas.forms;

import lombok.Getter;

@Getter
public class IndeferirPagamentoForm {

    private boolean aprovar;
    private String motivoIndeferimento;
}
