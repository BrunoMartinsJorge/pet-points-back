package br.com.api.petpoints.shared.features.notificacoes.form;

import br.com.api.petpoints.shared.enums.TiposNotificacoesEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NovaNotificacaoForm {

    private Long idDestinatario;
    private String titulo;
    private String mensagem;
    private TiposNotificacoesEnum tipo;
}
