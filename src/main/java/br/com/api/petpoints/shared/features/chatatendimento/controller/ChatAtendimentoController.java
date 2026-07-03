package br.com.api.petpoints.shared.features.chatatendimento.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.shared.features.chatatendimento.dto.ChatMensagemDto;
import br.com.api.petpoints.shared.features.chatatendimento.forms.MensagemAtendimentoForm;
import br.com.api.petpoints.shared.features.chatatendimento.service.ChatAtendimentoService;
import br.com.api.petpoints.shared.models.UsuarioModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatAtendimentoController {

    private final ChatAtendimentoService chatAtendimentoService;
    private final SimpMessagingTemplate template;

    @MessageMapping("/chat-atendimento/send")
    public void enviar(@Payload MensagemAtendimentoForm form, Principal principal) {
        ChatMensagemDto dto = this.chatAtendimentoService.enviarMensagem(form, idUsuario(principal));
        this.template.convertAndSend("/topic/chat-atendimento/" + dto.getIdChat(), dto);
    }

    @GetMapping("/chat-atendimento/{idChat}/mensagens")
    @ResponseBody
    public ResponseEntity<List<ChatMensagemDto>> mensagens(@PathVariable Long idChat,
                                                           HttpServletRequest request) {
        Long idUsuario = new TokenModel(request.getHeader("Authorization")).getIdUsuario();
        return ResponseEntity.ok(this.chatAtendimentoService.historico(idChat, idUsuario));
    }

    private Long idUsuario(Principal principal) {
        if (principal instanceof Authentication auth && auth.getPrincipal() instanceof UsuarioModel usuario) {
            return usuario.getId();
        }
        throw new RuntimeException("Usuário não autenticado no WebSocket!");
    }
}
