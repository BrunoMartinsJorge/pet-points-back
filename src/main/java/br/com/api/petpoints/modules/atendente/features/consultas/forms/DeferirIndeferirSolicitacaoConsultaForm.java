package br.com.api.petpoints.modules.atendente.features.consultas.forms;

import lombok.Getter;

@Getter
public class DeferirIndeferirSolicitacaoConsultaForm {
    private Long idSolicitacao;
    private String motivoIndeferimento;
}
