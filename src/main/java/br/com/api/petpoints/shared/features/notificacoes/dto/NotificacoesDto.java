package br.com.api.petpoints.shared.features.notificacoes.dto;

import br.com.api.petpoints.shared.enums.TiposNotificacoesEnum;
import br.com.api.petpoints.shared.models.NotificacaoModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificacoesDto {
    private Long id;
    private String titulo;
    private String mensagem;
    private LocalDateTime data;
    private boolean lida;
    private TiposNotificacoesEnum tipo;

    public NotificacoesDto(NotificacaoModel notificacao) {
        this.id = notificacao.getId();
        this.titulo = notificacao.getTitulo();
        this.mensagem = notificacao.getConteudo();
        this.data = notificacao.getEnviadoEm();
        this.lida = notificacao.isVisto();
        this.tipo = notificacao.getTipo();
    }

    public static List<NotificacoesDto> convert(List<NotificacaoModel> notificacoes) {
        return notificacoes.stream().map(NotificacoesDto::new).toList();
    }
}
