package br.com.api.petpoints.shared.features.chatinterno.service;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.modules.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.shared.enums.StatusPerfilEnum;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.features.chatinterno.dto.MensagemInternaDto;
import br.com.api.petpoints.shared.features.chatinterno.dto.UsuariosInternosDto;
import br.com.api.petpoints.shared.features.chatinterno.form.MensagemInternaForm;
import br.com.api.petpoints.shared.models.ChatModel;
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
    private final SimpMessagingTemplate template;

    private void verificarPermissaoFuncionario(Long idUsuario) {
        UsuarioModel usuario = this.getUsuarioPorId(idUsuario);
        if (usuario.getPermissao() == TipoUsuario.C)
            throw new RuntimeException("Usuário com permissão inválida!"); // Illegal;
    }

    private UsuarioModel getUsuarioPorId(Long idUsuario) {
        return this.usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: " + idUsuario + " não encontrado!"));
    }

    public List<UsuariosInternosDto> listarFuncionarios(Long idUsuario) {
        this.verificarPermissaoFuncionario(idUsuario);
        return null;
    }

    /*private List<UsuariosInternosDto> listarUsuariosChats(Long idUsuario) {
        List<UsuarioModel> funcionarios = this.usuarioRepository.findAllByPermissaoNotAndIdNot(TipoUsuario.C, idUsuario);
        for (UsuarioModel funcionario : funcionarios) {
            List<ChatModel> chat = this.chatParticipanteRepository.buscarChatEntreUsuarios(idUsuario, funcionario.getId());
        }
    }*/

    /*public void processarMensagem(MensagemInternaForm payload, Long idUsuario) {
        this.verificarUsuariosValidos(idUsuario, payload.getIdDestinatario());

        MensagemInternaDto msg = this.enviarMensagem(payload, idUsuario);

        template.convertAndSend("/topic/chat-interno/" + msg.getIdSessao(), msg);

        NotificationDto notif = new NotificationDto(
                "Nova mensagem!",
                msg.getMensagem(),
                msg.getRemetente(),
                msg.getIdDestinatario()
        );

        template.convertAndSend("/topic/notification/" + msg.getIdDestinatario(), notif);
    }*/

    private void verificarUsuariosValidos(Long idUsuario, Long idDestinatario) {
        UsuarioModel usuario = this.getUsuarioPorId(idUsuario);
        UsuarioModel destinatario = this.getUsuarioPorId(idDestinatario);
        if (usuario.getStatusPerfilEnum() == StatusPerfilEnum.D)
            throw new RuntimeException("Remetente está com perfil desabilitado!");
        if (destinatario.getStatusPerfilEnum() == StatusPerfilEnum.D)
            throw new RuntimeException("Destinatário está com perfil desabilitado!");
    }

    @Transactional
    protected MensagemInternaDto enviarMensagem(MensagemInternaForm payload, Long idUsuario) {
        ChatModel chat = this.chatRepository.findById(payload.getIdChat()).orElseThrow(() -> new ObjectNotFoundException("Chat com ID: " + payload.getIdChat() + " não encontrado!"));
        /*private Long id;
        private ChatModel chat;
        private UsuarioModel remetente;
        private String conteudo;
        private LocalDateTime enviadoEm;*/
        return null;
    }
}
