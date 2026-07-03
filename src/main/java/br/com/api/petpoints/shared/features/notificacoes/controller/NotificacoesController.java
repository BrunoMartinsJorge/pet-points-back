package br.com.api.petpoints.shared.features.notificacoes.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.shared.features.notificacoes.dto.NotificacoesDto;
import br.com.api.petpoints.shared.features.notificacoes.form.NovaNotificacaoForm;
import br.com.api.petpoints.shared.features.notificacoes.service.NotificacoesServiceImpl;
import br.com.api.petpoints.shared.models.UsuarioModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/ws/notificacoes")
@RequiredArgsConstructor
public class NotificacoesController {

    private final NotificacoesServiceImpl notificacoesService;
    private final SimpMessagingTemplate template;

    private Long getIdUsuario(Principal principal) {
        if (principal == null)
            throw new RuntimeException("Usuário não autenticado no WebSocket");
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;
        UsuarioModel user = (UsuarioModel) authenticationToken.getPrincipal();
        if (user == null)
            throw new UsuarioNaoEncontrado("Usuário não encontrado!");
        return user.getId();
    }

    @MessageMapping("/listar")
    public void listarNotificacoesPorUsuario(Principal principal) {
        Long idUsuario = this.getIdUsuario(principal);

        List<NotificacoesDto> lista =
                this.notificacoesService.buscarNotificacoesPorUsuario(idUsuario);

        this.template.convertAndSend(
                "/topic/notificacoes/" + idUsuario,
                lista
        );
    }

    @PutMapping("/marcar-lidas")
    public void marcarTodasComoLidas(HttpServletRequest request, @RequestBody List<Long> idNotificacoes) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        this.notificacoesService.marcarNotificacoesComoLidas(idNotificacoes);

        List<NotificacoesDto> lista =
                this.notificacoesService.buscarNotificacoesPorUsuario(token.getIdUsuario());

        this.template.convertAndSend(
                "/topic/notificacoes/" + token.getIdUsuario(),
                lista
        );
    }

    @MessageMapping("/send")
    public void enviarNotificacao(NovaNotificacaoForm form) {
        NotificacoesDto dto = this.notificacoesService.enviarNovaNotificacao(form);
        this.template.convertAndSend("/topic/notificacoes/" + form.getIdDestinatario(), dto);
    }
}
