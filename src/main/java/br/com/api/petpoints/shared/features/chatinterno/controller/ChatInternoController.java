package br.com.api.petpoints.shared.features.chatinterno.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.shared.features.chatinterno.dto.MensagemInternaDto;
import br.com.api.petpoints.shared.features.chatinterno.dto.UsuariosInternosDto;
import br.com.api.petpoints.shared.features.chatinterno.form.MensagemInternaForm;
import br.com.api.petpoints.shared.features.chatinterno.service.ChatInternoService;
import br.com.api.petpoints.shared.models.UsuarioModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatInternoController {

    private final ChatInternoService chatInternoService;
    private final SimpMessagingTemplate template;

    @GetMapping("/chat-interno/funcionarios")
    @ResponseBody
    public ResponseEntity<List<UsuariosInternosDto>> listarFuncionarios(HttpServletRequest request) {
        return ResponseEntity.ok(this.chatInternoService.listarFuncionarios(idUsuario(request)));
    }

    @PostMapping("/chat-interno/abrir/{idDestinatario}")
    @ResponseBody
    public ResponseEntity<Long> abrir(@PathVariable Long idDestinatario, HttpServletRequest request) {
        return ResponseEntity.ok(this.chatInternoService.abrirChat(idDestinatario, idUsuario(request)));
    }

    @GetMapping("/chat-interno/{idChat}/mensagens")
    @ResponseBody
    public ResponseEntity<List<MensagemInternaDto>> mensagens(@PathVariable Long idChat,
                                                              HttpServletRequest request) {
        return ResponseEntity.ok(this.chatInternoService.historico(idChat, idUsuario(request)));
    }

    @MessageMapping("/chat-interno/send")
    public void enviar(@Payload MensagemInternaForm form, Principal principal) {
        MensagemInternaDto dto = this.chatInternoService.processarMensagem(form, idUsuario(principal));
        this.template.convertAndSend("/topic/chat-interno/" + dto.getIdChat(), dto);
    }

    private Long idUsuario(HttpServletRequest request) {
        return new TokenModel(request.getHeader("Authorization")).getIdUsuario();
    }

    private Long idUsuario(Principal principal) {
        if (principal instanceof Authentication auth && auth.getPrincipal() instanceof UsuarioModel usuario) {
            return usuario.getId();
        }
        throw new RuntimeException("Usuário não autenticado no WebSocket!");
    }
}
