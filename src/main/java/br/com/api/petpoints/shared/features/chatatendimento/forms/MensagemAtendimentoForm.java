package br.com.api.petpoints.shared.features.chatatendimento.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MensagemAtendimentoForm {

    private String mensagem;
    private Long idChat;
}
