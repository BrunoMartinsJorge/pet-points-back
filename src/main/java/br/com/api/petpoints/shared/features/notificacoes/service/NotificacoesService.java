package br.com.api.petpoints.shared.features.notificacoes.service;

import br.com.api.petpoints.shared.features.notificacoes.dto.NotificacoesDto;
import br.com.api.petpoints.shared.features.notificacoes.form.NovaNotificacaoForm;

import java.util.List;

public interface NotificacoesService {

    List<NotificacoesDto> buscarNotificacoesPorUsuario(Long idUsuario);
    NotificacoesDto enviarNovaNotificacao(NovaNotificacaoForm form);
    void marcarNotificacoesComoLidas(List<Long> idNotificacoes);
}
