package br.com.api.petpoints.shared.features.chatatendimento.service;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.modules.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.shared.enums.StatusAtendimentoEnum;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.features.chatatendimento.dto.ChatMensagemDto;
import br.com.api.petpoints.shared.features.chatatendimento.forms.MensagemAtendimentoForm;
import br.com.api.petpoints.shared.models.AtendimentoModel;
import br.com.api.petpoints.shared.models.ChatModel;
import br.com.api.petpoints.shared.models.MensagemModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.AtendimentoRepository;
import br.com.api.petpoints.shared.repository.ChatRepository;
import br.com.api.petpoints.shared.repository.MensagemRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatAtendimentoService {

    private final MensagemRepository mensagemRepository;
    private final ChatRepository chatRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final UsuarioRepository usuarioRepository;

    private AtendimentoModel getAtendimentoPorId(Long idChat) {
        return this.atendimentoRepository.findByChat_Id(idChat).orElseThrow(() -> new ObjectNotFoundException("Chat de atendimento com ID: " + idChat + " não encontrado!"));
    }

    private UsuarioModel getUsuarioPorId(Long idUsuario) {
        return this.usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: " + idUsuario + " não encontrado!"));
    }

    @Transactional
    public ChatMensagemDto enviarMensagem(MensagemAtendimentoForm form, Long idUsuario) {
        UsuarioModel usuario = this.getUsuarioPorId(idUsuario);
        ChatModel chat = this.getAtendimentoPorId(idUsuario).getChat();
        this.chatAtendimentoPodeReceberMensagem(form.getIdChat(), usuario.getId());
        return new ChatMensagemDto(this.mensagemRepository.save(new MensagemModel(chat, usuario, form.getMensagem())), idUsuario);
    }

    @Transactional
    protected void chatAtendimentoPodeReceberMensagem(Long idChat, Long idUsuario) {
        UsuarioModel usuario = this.getUsuarioPorId(idUsuario);
        AtendimentoModel chat = this.getAtendimentoPorId(idChat);
        if (chat.getStatus() == StatusAtendimentoEnum.FINALIZADO)
            throw new RuntimeException("O chat de atendimento está finalizado!");
        if (usuario.getPermissao() == TipoUsuario.C && chat.getStatus() != StatusAtendimentoEnum.EM_ANDAMENTO)
            throw new RuntimeException("O chat em questão não foi iniciado por um atendente, aguarde!");
        if(usuario.getPermissao() == TipoUsuario.A) {
            if(chat.getStatus() == StatusAtendimentoEnum.PENDENTE){
                chat.setStatus(StatusAtendimentoEnum.EM_ANDAMENTO);
                this.atendimentoRepository.save(chat);
            }
        }
    }
}
