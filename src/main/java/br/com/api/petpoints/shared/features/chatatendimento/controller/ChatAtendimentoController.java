package br.com.api.petpoints.shared.features.chatatendimento.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.shared.features.chatatendimento.dto.AtendimentoDto;
import br.com.api.petpoints.shared.features.chatatendimento.dto.ChatAtendimentoDto;
import br.com.api.petpoints.shared.features.chatatendimento.dto.ChatMensagemDto;
import br.com.api.petpoints.shared.features.chatatendimento.dto.SolicitacoesAtendimentosDto;
import br.com.api.petpoints.shared.features.chatatendimento.forms.MensagemAtendimentoForm;
import br.com.api.petpoints.shared.features.chatatendimento.service.ChatAtendimentoService;
import br.com.api.petpoints.shared.form.AvaliacaoForm;
import br.com.api.petpoints.shared.models.UsuarioModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatAtendimentoController {

    private static final String TOPICO_SESSAO = "/topic/chat-atendimento/";

    private final ChatAtendimentoService chatAtendimentoService;
    private final SimpMessagingTemplate template;

    // -------------------------------------------------------------- cliente (REST)

    @GetMapping("/chat-atendimento/cliente/buscar-atendimentos")
    @ResponseBody
    public ResponseEntity<List<AtendimentoDto>> buscarAtendimentosCliente(HttpServletRequest request) {
        return ResponseEntity.ok(this.chatAtendimentoService.listarAtendimentosCliente(idUsuario(request)));
    }

    @PostMapping("/chat-atendimento/cliente/solicitar-atendimento")
    @ResponseBody
    public ResponseEntity<Void> solicitarAtendimento(@RequestParam String mensagem,
                                                     HttpServletRequest request) {
        this.chatAtendimentoService.solicitarAtendimento(mensagem, idUsuario(request));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/chat-atendimento/cliente/buscar-mensagens-chat-atendimento/{idChat}")
    @ResponseBody
    public ResponseEntity<List<ChatMensagemDto>> buscarMensagensCliente(@PathVariable Long idChat,
                                                                        HttpServletRequest request) {
        return ResponseEntity.ok(this.chatAtendimentoService.buscarMensagens(idChat, idUsuario(request)));
    }

    @GetMapping("/chat-atendimento/selecionar-atendimento/{idChat}")
    @ResponseBody
    public ResponseEntity<ChatAtendimentoDto> buscarAtendimentoPorChatId(@PathVariable Long idChat) {
        return ResponseEntity.ok(this.chatAtendimentoService.buscarAtendimentoSelecionado(idChat));
    }

    @PostMapping("/chat-atendimento/cliente/{idChat}")
    public ResponseEntity<Void> finalizarAtendimento(@PathVariable Long idChat, @RequestBody AvaliacaoForm form, HttpServletRequest request) {
        this.chatAtendimentoService.finalizarAtendimento(this.idUsuario(request), idChat, form);
        return ResponseEntity.ok().build();
    }

    // ------------------------------------------------------------ atendente (REST)

    @GetMapping("/chat-atendimento/atendente/listar-solicitacoes-atendimentos")
    @ResponseBody
    public ResponseEntity<List<SolicitacoesAtendimentosDto>> listarSolicitacoes() {
        return ResponseEntity.ok(this.chatAtendimentoService.listarSolicitacoes());
    }

    @PutMapping("/chat-atendimento/atendente/aceitar-solicitacao-atendimento/{idSolicitacao}")
    @ResponseBody
    public ResponseEntity<ChatAtendimentoDto> aceitarSolicitacao(@PathVariable Long idSolicitacao,
                                                                 HttpServletRequest request) {
        return ResponseEntity.ok(
                this.chatAtendimentoService.aceitarSolicitacao(idSolicitacao, idUsuario(request)));
    }

    @GetMapping("/chat-atendimento/atendente/meus-atendimentos")
    @ResponseBody
    public ResponseEntity<List<ChatAtendimentoDto>> meusAtendimentos(HttpServletRequest request) {
        return ResponseEntity.ok(this.chatAtendimentoService.listarMeusAtendimentos(idUsuario(request)));
    }

    // ------------------------------------------------------------- histórico (REST)

    /** Endpoint compartilhado usado pelo atendente (GET /chat-atendimento/{idChat}/mensagens). */
    @GetMapping("/chat-atendimento/{idChat}/mensagens")
    @ResponseBody
    public ResponseEntity<List<ChatMensagemDto>> mensagens(@PathVariable Long idChat,
                                                           HttpServletRequest request) {
        return ResponseEntity.ok(this.chatAtendimentoService.buscarMensagens(idChat, idUsuario(request)));
    }

    // ---------------------------------------------------------------- envio (WS)

    /** Cliente e atendente publicam aqui; a mensagem é persistida e reemitida no topico da sessao. */
    @MessageMapping("/chat-atendimento/send")
    public void enviar(@Payload MensagemAtendimentoForm form, Principal principal) {
        log.info("Mensagem de atendimento recebida! {}", form);
        ChatMensagemDto dto = this.chatAtendimentoService.processarMensagem(form, idUsuario(principal));
        this.template.convertAndSend(TOPICO_SESSAO + dto.getIdChat(), dto);
    }

    // ---------------------------------------------------------------- helpers

    private Long idUsuario(HttpServletRequest request) {
        return new TokenModel(request.getHeader("Authorization")).getIdUsuario();
    }

    private Long idUsuario(Principal principal) {
        if (principal instanceof Authentication auth && auth.getPrincipal() instanceof UsuarioModel usuario) {
            return usuario.getId();
        }
        throw new RuntimeException("Usuario nao autenticado no WebSocket!");
    }
}
