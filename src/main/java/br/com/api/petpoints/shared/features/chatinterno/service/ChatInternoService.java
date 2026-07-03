package br.com.api.petpoints.shared.features.chatinterno.service;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.shared.enums.StatusPerfilEnum;
import br.com.api.petpoints.shared.enums.TipoChatEnum;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.features.chatinterno.dto.MensagemInternaDto;
import br.com.api.petpoints.shared.features.chatinterno.dto.UsuariosInternosDto;
import br.com.api.petpoints.shared.features.chatinterno.form.MensagemInternaForm;
import br.com.api.petpoints.shared.models.ChatModel;
import br.com.api.petpoints.shared.models.ChatParticipanteModel;
import br.com.api.petpoints.shared.models.MensagemModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.ChatParticipanteRepository;
import br.com.api.petpoints.shared.repository.ChatRepository;
import br.com.api.petpoints.shared.repository.MensagemRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatInternoService {

    private final UsuarioRepository usuarioRepository;
    private final MensagemRepository mensagemRepository;
    private final ChatRepository chatRepository;
    private final ChatParticipanteRepository chatParticipanteRepository;

    @Transactional
    public List<UsuariosInternosDto> listarFuncionarios(Long idUsuario) {
        this.verificarPermissaoFuncionario(idUsuario);

        List<UsuarioModel> funcionarios =
                this.usuarioRepository.findAllByPermissaoNotAndIdNot(TipoUsuario.C, idUsuario);

        return funcionarios.stream().map(funcionario -> {
            Long idChat = this.chatParticipanteRepository
                    .buscarChatEntreUsuarios(idUsuario, funcionario.getId())
                    .stream().findFirst().map(ChatModel::getId).orElse(null);
            return new UsuariosInternosDto(funcionario, idChat);
        }).toList();
    }

    @Transactional
    public Long abrirChat(Long idDestinatario, Long idUsuario) {
        this.verificarUsuariosValidos(idUsuario, idDestinatario);
        return this.getOuCriarChat(idUsuario, idDestinatario).getId();
    }

    /** Histórico do chat, validando que o solicitante participa dele (UC036 / T1). */
    @Transactional
    public List<MensagemInternaDto> historico(Long idChat, Long idUsuario) {
        this.exigirParticipante(idChat, idUsuario);

        List<UsuarioModel> participantes = this.participantesDoChat(idChat);
        List<MensagemModel> mensagens =
                this.mensagemRepository.findByChat_IdOrderByEnviadoEmAsc(idChat);

        return mensagens.stream().map(msg -> {
            UsuarioModel remetente = msg.getRemetente();
            UsuarioModel destinatario = participantes.stream()
                    .filter(p -> !p.getId().equals(remetente.getId()))
                    .findFirst().orElse(remetente);
            return new MensagemInternaDto(msg, remetente, destinatario);
        }).toList();
    }

    // ---------------------------------------------------------------- envio (WS)

    /** UC020 – persiste a mensagem e devolve o DTO pronto para publicar no tópico. */
    @Transactional
    public MensagemInternaDto processarMensagem(MensagemInternaForm payload, Long idUsuario) {
        this.verificarUsuariosValidos(idUsuario, payload.getIdDestinatario());

        UsuarioModel remetente = this.getUsuarioPorId(idUsuario);
        UsuarioModel destinatario = this.getUsuarioPorId(payload.getIdDestinatario());

        ChatModel chat = payload.getIdChat() != null
                ? this.chatRepository.findById(payload.getIdChat())
                .orElseThrow(() -> new ObjectNotFoundException("Chat com ID: " + payload.getIdChat() + " não encontrado!"))
                : this.getOuCriarChat(idUsuario, payload.getIdDestinatario());

        this.exigirParticipante(chat.getId(), idUsuario);

        MensagemModel salva =
                this.mensagemRepository.save(new MensagemModel(chat, remetente, payload.getMensagem()));

        return new MensagemInternaDto(salva, remetente, destinatario);
    }

    private ChatModel getOuCriarChat(Long idA, Long idB) {
        return this.chatParticipanteRepository.buscarChatEntreUsuarios(idA, idB)
                .stream().findFirst()
                .orElseGet(() -> {
                    ChatModel chat = this.chatRepository.save(new ChatModel(null, TipoChatEnum.INTERNO));
                    this.chatParticipanteRepository.save(
                            new ChatParticipanteModel(null, chat, this.getUsuarioPorId(idA)));
                    this.chatParticipanteRepository.save(
                            new ChatParticipanteModel(null, chat, this.getUsuarioPorId(idB)));
                    return chat;
                });
    }

    private List<UsuarioModel> participantesDoChat(Long idChat) {
        return this.chatParticipanteRepository.findByChat_Id(idChat).stream()
                .map(ChatParticipanteModel::getParticipante)
                .toList();
    }

    private void exigirParticipante(Long idChat, Long idUsuario) {
        if (!this.chatParticipanteRepository.existsByChat_IdAndParticipante_Id(idChat, idUsuario)) {
            throw new RuntimeException("Você não participa deste chat!");
        }
    }

    private void verificarPermissaoFuncionario(Long idUsuario) {
        UsuarioModel usuario = this.getUsuarioPorId(idUsuario);
        if (usuario.getPermissao() == TipoUsuario.C)
            throw new RuntimeException("Usuário com permissão inválida!");
    }

    private void verificarUsuariosValidos(Long idUsuario, Long idDestinatario) {
        this.verificarPermissaoFuncionario(idUsuario);
        this.verificarPermissaoFuncionario(idDestinatario);
        UsuarioModel usuario = this.getUsuarioPorId(idUsuario);
        UsuarioModel destinatario = this.getUsuarioPorId(idDestinatario);
        if (usuario.getStatusPerfilEnum() == StatusPerfilEnum.D)
            throw new RuntimeException("Remetente está com perfil desabilitado!");
        if (destinatario.getStatusPerfilEnum() == StatusPerfilEnum.D)
            throw new RuntimeException("Destinatário está com perfil desabilitado!");
    }

    private UsuarioModel getUsuarioPorId(Long idUsuario) {
        return this.usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: " + idUsuario + " não encontrado!"));
    }
}
