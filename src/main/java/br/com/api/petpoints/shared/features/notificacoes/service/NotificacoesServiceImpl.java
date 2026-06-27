package br.com.api.petpoints.shared.features.notificacoes.service;

import br.com.api.petpoints.modules.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.shared.enums.TiposNotificacoesEnum;
import br.com.api.petpoints.shared.features.notificacoes.dto.NotificacoesDto;
import br.com.api.petpoints.shared.features.notificacoes.form.NovaNotificacaoForm;
import br.com.api.petpoints.shared.models.NotificacaoModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.NotificacaoRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificacoesServiceImpl implements NotificacoesService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public List<NotificacoesDto> buscarNotificacoesPorUsuario(Long idUsuario) {
        List<NotificacaoModel> notificacoes = this.notificacaoRepository.findByPara_Id(idUsuario).stream().filter(notificacao -> !notificacao.isVisto()).toList();
        return NotificacoesDto.convert(notificacoes);
    }

    @Override
    @Transactional
    public NotificacoesDto enviarNovaNotificacao(NovaNotificacaoForm form) {
        UsuarioModel destinatario = this.usuarioRepository.findById(form.getIdDestinatario()).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: " + form.getIdDestinatario() + " não encontrado!"));
        NotificacaoModel notificacao = this.notificacaoRepository.save(new NotificacaoModel(form, destinatario));
        return new NotificacoesDto(notificacao);
    }

    @Override
    public void marcarNotificacoesComoLidas(List<Long> idNotificacoes) {
        for (Long idNotificacao : idNotificacoes) {
            Optional<NotificacaoModel> notificacao_op = this.notificacaoRepository.findById(idNotificacao);
            if (notificacao_op.isEmpty()) return;
            NotificacaoModel notificacao = notificacao_op.get();
            notificacao.setVisto(true);
            this.notificacaoRepository.save(notificacao);
        }
    }
}
