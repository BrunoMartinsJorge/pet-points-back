package br.com.api.petpoints.shared.features.chatatendimento.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.shared.dto.AvaliacoesDto;
import br.com.api.petpoints.shared.features.chatatendimento.dto.*;
import br.com.api.petpoints.shared.features.chatatendimento.forms.MensagemAtendimentoForm;
import br.com.api.petpoints.shared.features.chatatendimento.service.ChatAtendimentoService;
import br.com.api.petpoints.shared.form.AvaliacaoForm;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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

    @GetMapping("/chat-atendimento/cliente/buscar-atendimentos")
    @ResponseBody
    public ResponseEntity<List<AtendimentoDto>> buscarAtendimentosCliente(HttpServletRequest request) {
        return ResponseEntity.ok(this.chatAtendimentoService.listarAtendimentosCliente(TokenUtils.getIdUsuario(request)));
    }

    @GetMapping("/chat-atendimento/cliente/equipe-atendimento")
    @ResponseBody
    public ResponseEntity<List<EquipeAtendimentoDto>> buscarEquipeAtendimento() {
        return ResponseEntity.ok(this.chatAtendimentoService.listarEquipeAtendimento());
    }

    @PostMapping("/chat-atendimento/cliente/solicitar-atendimento")
    @ResponseBody
    public ResponseEntity<Void> solicitarAtendimento(@RequestParam String mensagem,
                                                     HttpServletRequest request) {
        this.chatAtendimentoService.solicitarAtendimento(mensagem, TokenUtils.getIdUsuario(request));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/chat-atendimento/cliente/buscar-mensagens-chat-atendimento/{idChat}")
    @ResponseBody
    public ResponseEntity<List<ChatMensagemDto>> buscarMensagensCliente(@PathVariable Long idChat,
                                                                        HttpServletRequest request) {
        return ResponseEntity.ok(this.chatAtendimentoService.buscarMensagens(idChat, TokenUtils.getIdUsuario(request)));
    }

    @GetMapping("/chat-atendimento/selecionar-atendimento/{idChat}")
    @ResponseBody
    public ResponseEntity<ChatAtendimentoDto> buscarAtendimentoPorChatId(@PathVariable Long idChat) {
        return ResponseEntity.ok(this.chatAtendimentoService.buscarAtendimentoSelecionado(idChat));
    }

    @PostMapping("/chat-atendimento/cliente/{idChat}")
    public ResponseEntity<Void> finalizarAtendimento(@PathVariable Long idChat, @RequestBody @Valid AvaliacaoForm form, HttpServletRequest request) {
        this.chatAtendimentoService.finalizarAtendimento(TokenUtils.getIdUsuario(request), idChat, form);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/chat-atendimento/avaliacao/{idChat}")
    public ResponseEntity<AvaliacoesDto> buscarAvaliacaoAtendimento(@PathVariable Long idChat, HttpServletRequest request) {
        return ResponseEntity.ok().body(this.chatAtendimentoService.buscarAvaliacaoPorAtendimento(idChat, TokenUtils.getIdUsuario(request)));
    }

    @GetMapping("/chat-atendimento/atendente/cards")
    public ResponseEntity<CardsAtendimentoAtendenteDto> buscarInformacoesCards(HttpServletRequest request) {
        return ResponseEntity.ok().body(this.chatAtendimentoService.buscarInformacoesCardsAtendente(TokenUtils.getIdUsuario(request)));
    }

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
                this.chatAtendimentoService.aceitarSolicitacao(idSolicitacao, TokenUtils.getIdUsuario(request)));
    }

    @GetMapping("/chat-atendimento/atendente/meus-atendimentos")
    @ResponseBody
    public ResponseEntity<List<ChatAtendimentoDto>> meusAtendimentos(HttpServletRequest request) {
        return ResponseEntity.ok(this.chatAtendimentoService.listarMeusAtendimentos(TokenUtils.getIdUsuario(request)));
    }

    @GetMapping("/chat-atendimento/{idChat}/mensagens")
    @ResponseBody
    public ResponseEntity<List<ChatMensagemDto>> mensagens(@PathVariable Long idChat,
                                                           HttpServletRequest request) {
        return ResponseEntity.ok(this.chatAtendimentoService.buscarMensagens(idChat, TokenUtils.getIdUsuario(request)));
    }

    @MessageMapping("/chat-atendimento/send")
    public void enviar(@Payload MensagemAtendimentoForm form, Principal principal) {
        log.info("Mensagem de atendimento recebida! {}", form);
        ChatMensagemDto dto = this.chatAtendimentoService.processarMensagem(form, TokenUtils.getIdUsuario(principal));
        this.template.convertAndSend(TOPICO_SESSAO + dto.getIdChat(), dto);
    }
}
