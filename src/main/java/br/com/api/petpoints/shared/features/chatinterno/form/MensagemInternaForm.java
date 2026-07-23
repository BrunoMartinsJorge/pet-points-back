package br.com.api.petpoints.shared.features.chatinterno.form;

import lombok.Getter;

@Getter
public class MensagemInternaForm {

    private Long idChat;
    private Long idDestinatario;
    private String mensagem;
}
