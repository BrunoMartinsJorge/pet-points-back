package br.com.api.petpoints.shared.features.chatatendimento.controller;

import br.com.api.petpoints.shared.features.chatatendimento.forms.MensagemAtendimentoForm;
import br.com.api.petpoints.shared.features.chatatendimento.service.ChatAtendimentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatAtendimentoController {

    private ChatAtendimentoService chatAtendimentoService;

    private Long getIdUsuario(Principal principal) {
        if (principal == null)
            throw new RuntimeException("Usuário não autenticado no WebSocket");
        return Long.valueOf(principal.getName());
    }

    @MessageMapping("/cliente/send")
    @SendTo("/topic/atendente")
    public Object clienteEnviaMensagem(@RequestBody MensagemAtendimentoForm form, Principal principal) {
        return this.chatAtendimentoService.enviarMensagem(form, this.getIdUsuario(principal));
    }
}
