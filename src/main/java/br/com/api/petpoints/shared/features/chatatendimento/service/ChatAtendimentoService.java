package br.com.api.petpoints.shared.features.chatatendimento.service;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatAtendimentoService {

    private final MensagemRepository mensagemRepository;
    private final ChatRepository chatRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public ChatMensagemDto enviarMensagem(MensagemAtendimentoForm form, Long idUsuario) {
        UsuarioModel usuario = this.getUsuarioPorId(idUsuario);
        AtendimentoModel atendimento = this.getAtendimentoPorChat(form.getIdChat());

        this.validarEnvio(atendimento, usuario);

        ChatModel chat = atendimento.getChat();
        MensagemModel salva =
                this.mensagemRepository.save(new MensagemModel(chat, usuario, form.getMensagem()));

        return new ChatMensagemDto(salva, idUsuario);
    }

    @Transactional
    public List<ChatMensagemDto> historico(Long idChat, Long idUsuario) {
        AtendimentoModel atendimento = this.getAtendimentoPorChat(idChat);
        boolean participa =
                atendimento.getCliente().getId().equals(idUsuario)
                        || (atendimento.getAtendente() != null
                        && atendimento.getAtendente().getId().equals(idUsuario));
        if (!participa) {
            throw new RuntimeException("Você não participa deste atendimento!");
        }
        return ChatMensagemDto.convert(
                this.mensagemRepository.findByChat_IdOrderByEnviadoEmAsc(idChat), idUsuario);
    }

    @Transactional
    protected void validarEnvio(AtendimentoModel atendimento, UsuarioModel usuario) {
        if (atendimento.getStatus() == StatusAtendimentoEnum.FINALIZADO)
            throw new RuntimeException("O chat de atendimento está finalizado!");

        // Cliente só pode escrever depois que um atendente iniciou (UC021).
        if (usuario.getPermissao() == TipoUsuario.C
                && atendimento.getStatus() != StatusAtendimentoEnum.EM_ANDAMENTO)
            throw new RuntimeException("O chat em questão não foi iniciado por um atendente, aguarde!");

        // Atendente que responde a um pendente assume e inicia o atendimento (UC021).
        if (usuario.getPermissao() == TipoUsuario.A
                && atendimento.getStatus() == StatusAtendimentoEnum.PENDENTE) {
            atendimento.setStatus(StatusAtendimentoEnum.EM_ANDAMENTO);
            atendimento.setAtendente(usuario);
            atendimento.setIniciadoEm(java.time.LocalDateTime.now());
            this.atendimentoRepository.save(atendimento);
        }
    }

    private AtendimentoModel getAtendimentoPorChat(Long idChat) {
        return this.atendimentoRepository.findByChat_Id(idChat)
                .orElseThrow(() -> new ObjectNotFoundException("Chat de atendimento com ID: " + idChat + " não encontrado!"));
    }

    private UsuarioModel getUsuarioPorId(Long idUsuario) {
        return this.usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: " + idUsuario + " não encontrado!"));
    }
}
