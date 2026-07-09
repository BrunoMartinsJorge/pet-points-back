package br.com.api.petpoints.shared.features.chatatendimento.service;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.enums.StatusAtendimentoEnum;
import br.com.api.petpoints.shared.enums.TipoChatEnum;
import br.com.api.petpoints.shared.enums.TiposNotificacoesEnum;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.features.chatatendimento.dto.*;
import br.com.api.petpoints.shared.features.chatatendimento.forms.MensagemAtendimentoForm;
import br.com.api.petpoints.shared.features.chatatendimento.forms.SolicitacaoAtendimentoForm;
import br.com.api.petpoints.shared.features.notificacoes.controller.NotificacoesController;
import br.com.api.petpoints.shared.features.notificacoes.form.NovaNotificacaoForm;
import br.com.api.petpoints.shared.form.AvaliacaoForm;
import br.com.api.petpoints.shared.models.*;
import br.com.api.petpoints.shared.repository.*;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatAtendimentoService {

    private static final String TOPICO_STATUS = "/topic/chat-atendimento/status/";

    private final MensagemRepository mensagemRepository;
    private final ChatRepository chatRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final NotificacoesController notificacoesController;
    private final SimpMessagingTemplate template;

    // ------------------------------------------------------------------ cliente

    /**
     * Atendimentos do cliente logado (com o nome do atendente, se ja houver).
     */
    @Transactional
    public List<AtendimentoDto> listarAtendimentosCliente(Long idCliente) {
        return this.atendimentoRepository.findAllByCliente_Id(idCliente)
                .stream().map(AtendimentoDto::new).toList();
    }

    /**
     * UC – cliente abre uma nova solicitacao de atendimento (status PENDENTE).
     */
    @Transactional
    public void solicitarAtendimento(String mensagem, Long idCliente) {
        if (mensagem == null || mensagem.isBlank())
            throw new RuntimeException("A mensagem da solicitacao nao pode estar vazia!");

        UsuarioModel cliente = this.getUsuario(idCliente);

        // O chat é salvo em cascata junto do atendimento (AtendimentoModel.chat = cascade ALL).
        ChatModel chat = new ChatModel(null, TipoChatEnum.ATENDIMENTO);
        AtendimentoModel atendimento = new AtendimentoModel(cliente, chat, mensagem);

        this.atendimentoRepository.save(atendimento);
    }

    // ------------------------------------------------------------------ atendente

    /**
     * Solicitacoes ainda sem atendente e com status PENDENTE.
     */
    @Transactional
    public List<SolicitacoesAtendimentosDto> listarSolicitacoes() {
        return this.atendimentoRepository
                .buscarSolicitacoesAtendimentos(StatusAtendimentoEnum.PENDENTE)
                .stream().map(SolicitacoesAtendimentosDto::new).toList();
    }

    /**
     * Atendente assume a solicitacao: define o atendente, muda o status e marca o inicio.
     */
    @Transactional
    public ChatAtendimentoDto aceitarSolicitacao(Long idAtendimento, Long idAtendente) {
        UsuarioModel atendente = this.getUsuario(idAtendente);

        AtendimentoModel atendimento = this.atendimentoRepository.findById(idAtendimento)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Atendimento com ID: " + idAtendimento + " nao encontrado!"));

        if (atendimento.getAtendente() != null)
            throw new RuntimeException("Este atendimento ja foi assumido por outro atendente!");

        atendimento.setAtendente(atendente);
        atendimento.setStatus(StatusAtendimentoEnum.EM_ANDAMENTO);
        atendimento.setIniciadoEm(LocalDateTime.now());

        atendimento = this.atendimentoRepository.save(atendimento);
        this.emitirStatus(atendimento);

        return new ChatAtendimentoDto(atendimento);
    }

    /**
     * Atendimentos que o atendente logado ja assumiu.
     */
    @Transactional
    public List<ChatAtendimentoDto> listarMeusAtendimentos(Long idAtendente) {
        return this.atendimentoRepository.findAllByAtendente_Id(idAtendente)
                .stream().map(ChatAtendimentoDto::new).toList();
    }

    // ------------------------------------------------------------------ mensagens

    /**
     * Historico do chat (usado para semear o WebSocket ao abrir a conversa).
     */
    @Transactional
    public List<ChatMensagemDto> buscarMensagens(Long idChat, Long idUsuario) {
        this.exigirParticipante(idChat, idUsuario);
        return ChatMensagemDto.convert(
                this.mensagemRepository.findByChat_IdOrderByEnviadoEmAsc(idChat), idUsuario);
    }

    @Transactional
    public void finalizarAtendimento(Long idUsuario, Long idChat, AvaliacaoForm form) {
        UsuarioModel cliente = this.getUsuario(idUsuario);
        AtendimentoModel atendimento = this.getAtendimentoPorChat(idChat);
        if (!cliente.equals(atendimento.getCliente()))
            throw new RuntimeException("Você não tem acesso ao atendimento em questão!");
        atendimento.setStatus(StatusAtendimentoEnum.FINALIZADO);
        AvaliacaoModel avaliacao = new AvaliacaoModel(form, cliente);
        avaliacao = this.avaliacaoRepository.save(avaliacao);
        atendimento.setAvaliacao(avaliacao);
        atendimento = this.atendimentoRepository.save(atendimento);
        this.emitirStatus(atendimento);
        NovaNotificacaoForm notificacao = new NovaNotificacaoForm(
                atendimento.getAtendente().getId(),
                "Finalizado",
                "Cliente finalizou atendimento!",
                TiposNotificacoesEnum.ATENDIMENTO
        );
        try {
            this.notificacoesController.enviarNotificacao(notificacao);
            log.info("Notificação de aprovação de pagamento enviada ao cliente!");
        } catch (Exception e) {
            log.error("Problema ao enviar notificação de aprovação de pagamento ao cliente!");
            throw new RuntimeException("Ocorreu um erro ao gerar notificação de cliente da consulta!");
        }
    }

    /**
     * Persiste a mensagem recebida via WebSocket e devolve o DTO pronto para publicar no topico.
     * O remetente é sempre o usuario autenticado no socket (cliente ou atendente).
     */
    @Transactional
    public ChatMensagemDto processarMensagem(MensagemAtendimentoForm form, Long idUsuario) {
        if (form.getMensagem() == null || form.getMensagem().isBlank())
            throw new RuntimeException("A mensagem nao pode estar vazia!");

        AtendimentoModel atendimento = this.getAtendimentoPorChat(form.getIdChat());
        this.exigirParticipante(atendimento, idUsuario);

        UsuarioModel remetente = this.getUsuario(idUsuario);

        // Se um atendente responde uma solicitacao ainda pendente, assume o atendimento.
        if (atendimento.getStatus() == StatusAtendimentoEnum.PENDENTE
                && remetente.getPermissao() == TipoUsuario.A) {
            atendimento.setAtendente(remetente);
            atendimento.setStatus(StatusAtendimentoEnum.EM_ANDAMENTO);
            atendimento.setIniciadoEm(LocalDateTime.now());
            atendimento = this.atendimentoRepository.save(atendimento);
            this.emitirStatus(atendimento);
        }

        MensagemModel salva = this.mensagemRepository.save(
                new MensagemModel(atendimento.getChat(), remetente, form.getMensagem()));

        return new ChatMensagemDto(salva, idUsuario);
    }

    public ChatAtendimentoDto buscarAtendimentoSelecionado(Long idChat) {
        AtendimentoModel atendimento = this.getAtendimentoPorChat(idChat);
        return new ChatAtendimentoDto(atendimento);
    }

    // ------------------------------------------------------------------ helpers

    /**
     * Publica a mudança de status no tópico do chat, para o lado que não fez a ação
     * (cliente quando o atendente aceita, atendente quando o cliente finaliza) atualizar
     * a tela em tempo real, sem precisar de reload ou de um novo GET.
     */
    private void emitirStatus(AtendimentoModel atendimento) {
        this.template.convertAndSend(
                TOPICO_STATUS + atendimento.getChat().getId(),
                new StatusAtendimentoDto(atendimento));
    }

    private AtendimentoModel getAtendimentoPorChat(Long idChat) {
        if (idChat == null)
            throw new RuntimeException("O idChat da mensagem nao foi informado!");
        return this.atendimentoRepository.findByChat_Id(idChat)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Atendimento do chat com ID: " + idChat + " nao encontrado!"));
    }

    private void exigirParticipante(Long idChat, Long idUsuario) {
        this.exigirParticipante(this.getAtendimentoPorChat(idChat), idUsuario);
    }

    /**
     * So o cliente dono da conversa ou o atendente que a assumiu podem ver/enviar.
     */
    private void exigirParticipante(AtendimentoModel atendimento, Long idUsuario) {
        boolean ehCliente = atendimento.getCliente() != null
                && atendimento.getCliente().getId().equals(idUsuario);
        boolean ehAtendente = atendimento.getAtendente() != null
                && atendimento.getAtendente().getId().equals(idUsuario);

        // Atendimento ainda pendente: qualquer atendente pode entrar (vai assumi-lo ao responder).
        boolean atendentePodeAssumir = atendimento.getAtendente() == null
                && atendimento.getStatus() == StatusAtendimentoEnum.PENDENTE
                && this.getUsuario(idUsuario).getPermissao() == TipoUsuario.A;

        if (!ehCliente && !ehAtendente && !atendentePodeAssumir)
            throw new RuntimeException("Voce nao participa deste atendimento!");
    }

    private UsuarioModel getUsuario(Long idUsuario) {
        return this.usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioNaoEncontrado(
                        "Usuario com ID: " + idUsuario + " nao encontrado!"));
    }
}
