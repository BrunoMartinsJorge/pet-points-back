package br.com.api.petpoints.shared.features.chatatendimento.service;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.domain.users.cliente.features.meuspagamentos.dto.PagamentosDto;
import br.com.api.petpoints.shared.dto.AvaliacoesDto;
import br.com.api.petpoints.shared.dto.SolicitacaoRemovidaDto;
import br.com.api.petpoints.shared.enums.StatusAtendimentoEnum;
import br.com.api.petpoints.shared.enums.TipoChatEnum;
import br.com.api.petpoints.shared.enums.TiposNotificacoesEnum;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.features.chatatendimento.dto.*;
import br.com.api.petpoints.shared.features.chatatendimento.forms.MensagemAtendimentoForm;
import br.com.api.petpoints.shared.features.notificacoes.controller.NotificacoesController;
import br.com.api.petpoints.shared.features.notificacoes.form.NovaNotificacaoForm;
import br.com.api.petpoints.shared.form.AvaliacaoForm;
import br.com.api.petpoints.shared.models.*;
import br.com.api.petpoints.shared.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatAtendimentoService {

    private static final String TOPICO_STATUS = "/topic/chat-atendimento/status/";
    private static final String TOPICO_STATUS_USUARIO = "/topic/chat-atendimento/status-usuario/";
    private static final String TOPICO_SOLICITACOES = "/topic/chat-atendimento/solicitacoes";
    private static final String TOPICO_SOLICITACOES_REMOVIDAS = "/topic/chat-atendimento/solicitacoes/removidas";

    private final MensagemRepository mensagemRepository;
    private final ChatRepository chatRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final NotificacoesController notificacoesController;
    private final SimpMessagingTemplate template;

    public CardsAtendimentoAtendenteDto buscarInformacoesCardsAtendente(Long idUsuario) {
        List<AtendimentoModel> atendimentos = this.atendimentoRepository.findAllByAtendente_Id(idUsuario);
        long qtdAndamento = atendimentos.stream().filter(atendimento -> atendimento.getStatus().equals(StatusAtendimentoEnum.EM_ANDAMENTO)).count();
        long qtdFinalizados = atendimentos.stream().filter(atendimento -> atendimento.getStatus().equals(StatusAtendimentoEnum.FINALIZADO)).count();
        List<AtendimentoModel> atendimentosAvaliados = atendimentos.stream().filter(atendimento -> atendimento.getAvaliacao() != null).toList();
        if (atendimentosAvaliados.isEmpty()) return new CardsAtendimentoAtendenteDto(qtdAndamento, qtdFinalizados, BigDecimal.ZERO);
        int somatoria = atendimentosAvaliados.stream().map(atendimento -> atendimento.getAvaliacao().getPontuacao()).reduce(0, Integer::sum);
        BigDecimal pontuacao = BigDecimal.valueOf(somatoria / atendimentosAvaliados.size())
                .setScale(2, RoundingMode.HALF_UP).max(BigDecimal.valueOf(5)).min(BigDecimal.valueOf(5));
        return new CardsAtendimentoAtendenteDto(qtdAndamento, qtdFinalizados, pontuacao);
    }

    @Transactional
    public List<AtendimentoDto> listarAtendimentosCliente(Long idCliente) {
        return this.atendimentoRepository.findAllByCliente_Id(idCliente)
                .stream().map(AtendimentoDto::new).toList();
    }

    @Transactional
    public void solicitarAtendimento(String mensagem, Long idCliente) {
        log.info("Iniciando nova solicitação de atendimento do cliente com ID: {}", idCliente);

        if (mensagem == null || mensagem.isBlank()) {
            log.warn("Solicitação de atendimento recusada: mensagem vazia (cliente ID: {})", idCliente);
            throw new RuntimeException("A mensagem da solicitacao nao pode estar vazia!");
        }

        UsuarioModel cliente = this.getUsuario(idCliente);

        ChatModel chat = new ChatModel(null, TipoChatEnum.ATENDIMENTO);
        AtendimentoModel atendimento = new AtendimentoModel(cliente, chat, mensagem);

        atendimento = this.atendimentoRepository.save(atendimento);
        log.info("Solicitação de atendimento ID: {} criada com sucesso pelo cliente {}", atendimento.getId(), cliente.getNome());

        this.publicarNovaSolicitacao(atendimento);
        this.notificarAtendentesNovaSolicitacao(atendimento);
    }

    @Transactional
    public List<SolicitacoesAtendimentosDto> listarSolicitacoes() {
        log.info("Listando solicitações de atendimento pendentes.");
        List<SolicitacoesAtendimentosDto> solicitacoes = this.atendimentoRepository
                .buscarSolicitacoesAtendimentos(StatusAtendimentoEnum.PENDENTE)
                .stream().map(SolicitacoesAtendimentosDto::new).toList();
        log.info("Foram encontradas {} solicitação(ões) pendente(s).", solicitacoes.size());
        return solicitacoes;
    }

    @Transactional
    public ChatAtendimentoDto aceitarSolicitacao(Long idAtendimento, Long idAtendente) {
        log.info("Atendente com ID: {} tentando aceitar a solicitação de atendimento ID: {}", idAtendente, idAtendimento);

        UsuarioModel atendente = this.getUsuario(idAtendente);

        AtendimentoModel atendimento = this.atendimentoRepository.findById(idAtendimento)
                .orElseThrow(() -> {
                    log.warn("Atendimento com ID: {} não encontrado ao aceitar solicitação.", idAtendimento);
                    return new ObjectNotFoundException(
                            "Atendimento com ID: " + idAtendimento + " nao encontrado!");
                });

        if (atendimento.getAtendente() != null) {
            log.warn("Solicitação ID: {} já havia sido assumida por outro atendente ({}).",
                    idAtendimento, atendimento.getAtendente().getNome());
            throw new RuntimeException("Este atendimento ja foi assumido por outro atendente!");
        }

        atendimento.setAtendente(atendente);
        atendimento.setStatus(StatusAtendimentoEnum.EM_ANDAMENTO);
        atendimento.setIniciadoEm(LocalDateTime.now());

        atendimento = this.atendimentoRepository.save(atendimento);
        log.info("Solicitação ID: {} assumida pelo atendente {} (ID: {}).",
                idAtendimento, atendente.getNome(), idAtendente);

        this.emitirStatus(atendimento);
        this.notificarAtendimentoAssumido(atendimento);

        return new ChatAtendimentoDto(atendimento);
    }

    @Transactional
    public List<ChatAtendimentoDto> listarMeusAtendimentos(Long idAtendente) {
        log.info("Listando atendimentos assumidos pelo atendente com ID: {}", idAtendente);
        List<ChatAtendimentoDto> atendimentos = this.atendimentoRepository.findAllByAtendente_Id(idAtendente)
                .stream().map(ChatAtendimentoDto::new).toList();
        log.info("Atendente com ID: {} possui {} atendimento(s).", idAtendente, atendimentos.size());
        return atendimentos;
    }

    @Transactional
    public List<ChatMensagemDto> buscarMensagens(Long idChat, Long idUsuario) {
        this.exigirParticipante(idChat, idUsuario);
        return ChatMensagemDto.convert(
                this.mensagemRepository.findByChat_IdOrderByEnviadoEmAsc(idChat), idUsuario);
    }

    @Transactional
    public void finalizarAtendimento(Long idUsuario, Long idChat, AvaliacaoForm form) {
        log.info("Cliente com ID: {} finalizando atendimento do chat ID: {}", idUsuario, idChat);
        UsuarioModel cliente = this.getUsuario(idUsuario);
        AtendimentoModel atendimento = this.getAtendimentoPorChat(idChat);
        if (!cliente.equals(atendimento.getCliente())) {
            log.warn("Cliente com ID: {} tentou finalizar um atendimento que não é dele (chat ID: {}).", idUsuario, idChat);
            throw new RuntimeException("Você não tem acesso ao atendimento em questão!");
        }
        atendimento.setStatus(StatusAtendimentoEnum.FINALIZADO);
        atendimento.setFinalizadoEm(LocalDateTime.now());
        AvaliacaoModel avaliacao = new AvaliacaoModel(form, cliente);
        avaliacao = this.avaliacaoRepository.save(avaliacao);
        atendimento.setAvaliacao(avaliacao);
        atendimento = this.atendimentoRepository.save(atendimento);
        log.info("Atendimento do chat ID: {} finalizado e avaliado pelo cliente {}.", idChat, cliente.getNome());

        StatusAtendimentoDto statusDto = new StatusAtendimentoDto(atendimento);
        Long idChatFinalizado = atendimento.getChat().getId();
        Long idClienteFinal = atendimento.getCliente() != null ? atendimento.getCliente().getId() : null;
        Long idAtendenteFinal = atendimento.getAtendente() != null ? atendimento.getAtendente().getId() : null;

        NovaNotificacaoForm notificacao = new NovaNotificacaoForm(
                idAtendenteFinal,
                "Finalizado",
                "Cliente finalizou atendimento!",
                TiposNotificacoesEnum.ATENDIMENTO
        );

        this.executarAposCommit(() -> {
            this.emitirStatus(statusDto, idChatFinalizado, idClienteFinal, idAtendenteFinal);
            try {
                this.notificacoesController.enviarNotificacao(notificacao);
                log.info("Notificação de finalização de atendimento enviada ao atendente ID: {}.", idAtendenteFinal);
            } catch (Exception e) {
                log.error("Problema ao enviar notificação de finalização de atendimento ao atendente ID: {}.",
                        idAtendenteFinal, e);
            }
        });
    }

    public AvaliacoesDto buscarAvaliacaoPorAtendimento(Long idChat, Long idUsuario) {
        log.info("Buscando avaliação do atendimento do chat ID: {} (usuário ID: {}).", idChat, idUsuario);
        this.exigirParticipante(idChat, idUsuario);
        AtendimentoModel atendimento = this.getAtendimentoPorChat(idChat);
        if (atendimento.getStatus() != StatusAtendimentoEnum.FINALIZADO || atendimento.getAvaliacao() == null) {
            log.info("Atendimento do chat ID: {} ainda não possui avaliação.", idChat);
            return new AvaliacoesDto();
        }
        return new AvaliacoesDto(atendimento.getAvaliacao());
    }

    @Transactional
    public ChatMensagemDto processarMensagem(MensagemAtendimentoForm form, Long idUsuario) {
        log.info("Processando mensagem do usuário ID: {} no chat ID: {}", idUsuario, form.getIdChat());

        if (form.getMensagem() == null || form.getMensagem().isBlank()) {
            log.warn("Mensagem recusada: conteúdo vazio (usuário ID: {}, chat ID: {}).", idUsuario, form.getIdChat());
            throw new RuntimeException("A mensagem nao pode estar vazia!");
        }

        AtendimentoModel atendimento = this.getAtendimentoPorChat(form.getIdChat());
        this.exigirParticipante(atendimento, idUsuario);

        UsuarioModel remetente = this.getUsuario(idUsuario);

        if (atendimento.getStatus() == StatusAtendimentoEnum.PENDENTE
                && remetente.getPermissao() == TipoUsuario.A) {
            log.info("Atendente {} (ID: {}) assumindo atendimento pendente ao responder o chat ID: {}.",
                    remetente.getNome(), idUsuario, form.getIdChat());
            atendimento.setAtendente(remetente);
            atendimento.setStatus(StatusAtendimentoEnum.EM_ANDAMENTO);
            atendimento.setIniciadoEm(LocalDateTime.now());
            atendimento = this.atendimentoRepository.save(atendimento);
            this.emitirStatus(atendimento);
            this.notificarAtendimentoAssumido(atendimento);
        }

        MensagemModel salva = this.mensagemRepository.save(
                new MensagemModel(atendimento.getChat(), remetente, form.getMensagem()));
        log.info("Mensagem ID: {} salva no chat ID: {} (remetente: {}).",
                salva.getId(), form.getIdChat(), remetente.getNome());

        return new ChatMensagemDto(salva, idUsuario);
    }

    public ChatAtendimentoDto buscarAtendimentoSelecionado(Long idChat) {
        log.info("Buscando atendimento selecionado pelo chat ID: {}", idChat);
        AtendimentoModel atendimento = this.getAtendimentoPorChat(idChat);
        return new ChatAtendimentoDto(atendimento);
    }

    private void emitirStatus(AtendimentoModel atendimento) {
        this.emitirStatus(
                new StatusAtendimentoDto(atendimento),
                atendimento.getChat().getId(),
                atendimento.getCliente() != null ? atendimento.getCliente().getId() : null,
                atendimento.getAtendente() != null ? atendimento.getAtendente().getId() : null);
    }

    private void emitirStatus(StatusAtendimentoDto dto, Long idChat, Long idCliente, Long idAtendente) {
        log.info("Emitindo status '{}' no tópico do chat ID: {}.", dto.getStatus(), idChat);
        this.template.convertAndSend(TOPICO_STATUS + idChat, dto);

        if (idCliente != null) {
            this.template.convertAndSend(TOPICO_STATUS_USUARIO + idCliente, dto);
            log.info("Status '{}' publicado no tópico pessoal do cliente ID: {}.", dto.getStatus(), idCliente);
        }
        if (idAtendente != null) {
            this.template.convertAndSend(TOPICO_STATUS_USUARIO + idAtendente, dto);
            log.info("Status '{}' publicado no tópico pessoal do atendente ID: {}.", dto.getStatus(), idAtendente);
        }
    }

    private void executarAposCommit(Runnable acao) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    acao.run();
                }
            });
        } else {
            acao.run();
        }
    }

    private void publicarNovaSolicitacao(AtendimentoModel atendimento) {
        log.info("Publicando nova solicitação ID: {} no tópico dos atendentes.", atendimento.getId());
        this.template.convertAndSend(
                TOPICO_SOLICITACOES,
                new SolicitacoesAtendimentosDto(atendimento));
    }

    private void notificarAtendentesNovaSolicitacao(AtendimentoModel atendimento) {
        List<UsuarioModel> atendentes = this.usuarioRepository.findAllByPermissao(TipoUsuario.A);
        log.info("Notificando {} atendente(s) sobre a nova solicitação ID: {}.", atendentes.size(), atendimento.getId());

        String nomeCliente = atendimento.getCliente() != null ? atendimento.getCliente().getNome() : "Cliente";

        for (UsuarioModel atendente : atendentes) {
            NovaNotificacaoForm notificacao = new NovaNotificacaoForm(
                    atendente.getId(),
                    "Nova solicitação de atendimento",
                    nomeCliente + " abriu uma nova solicitação de atendimento!",
                    TiposNotificacoesEnum.ATENDIMENTO
            );
            try {
                this.notificacoesController.enviarNotificacao(notificacao);
                log.info("Notificação de nova solicitação enviada ao atendente ID: {}.", atendente.getId());
            } catch (Exception e) {
                log.error("Falha ao notificar o atendente ID: {} sobre a solicitação ID: {}.",
                        atendente.getId(), atendimento.getId(), e);
            }
        }
    }

    private void notificarAtendimentoAssumido(AtendimentoModel atendimento) {
        log.info("Publicando remoção da solicitação ID: {} no tópico dos atendentes.", atendimento.getId());
        this.template.convertAndSend(
                TOPICO_SOLICITACOES_REMOVIDAS,
                new SolicitacaoRemovidaDto(atendimento));

        if (atendimento.getCliente() == null) {
            log.warn("Atendimento ID: {} sem cliente associado; notificação ao cliente ignorada.", atendimento.getId());
            return;
        }

        String nomeAtendente = atendimento.getAtendente() != null ? atendimento.getAtendente().getNome() : "Um atendente";
        NovaNotificacaoForm notificacao = new NovaNotificacaoForm(
                atendimento.getCliente().getId(),
                "Atendimento aceito",
                nomeAtendente + " iniciou o seu atendimento!",
                TiposNotificacoesEnum.ATENDIMENTO
        );
        try {
            this.notificacoesController.enviarNotificacao(notificacao);
            log.info("Notificação de atendimento aceito enviada ao cliente ID: {}.", atendimento.getCliente().getId());
        } catch (Exception e) {
            log.error("Falha ao notificar o cliente ID: {} sobre o aceite do atendimento ID: {}.",
                    atendimento.getCliente().getId(), atendimento.getId(), e);
        }
    }

    private AtendimentoModel getAtendimentoPorChat(Long idChat) {
        if (idChat == null) {
            log.warn("Tentativa de buscar atendimento com idChat nulo.");
            throw new RuntimeException("O idChat da mensagem nao foi informado!");
        }
        return this.atendimentoRepository.findByChat_Id(idChat)
                .orElseThrow(() -> {
                    log.warn("Atendimento do chat com ID: {} não encontrado.", idChat);
                    return new ObjectNotFoundException(
                            "Atendimento do chat com ID: " + idChat + " nao encontrado!");
                });
    }

    private void exigirParticipante(Long idChat, Long idUsuario) {
        this.exigirParticipante(this.getAtendimentoPorChat(idChat), idUsuario);
    }

    private void exigirParticipante(AtendimentoModel atendimento, Long idUsuario) {
        boolean ehCliente = atendimento.getCliente() != null
                && atendimento.getCliente().getId().equals(idUsuario);
        boolean ehAtendente = atendimento.getAtendente() != null
                && atendimento.getAtendente().getId().equals(idUsuario);

        boolean atendentePodeAssumir = atendimento.getAtendente() == null
                && atendimento.getStatus() == StatusAtendimentoEnum.PENDENTE
                && this.getUsuario(idUsuario).getPermissao() == TipoUsuario.A;

        if (!ehCliente && !ehAtendente && !atendentePodeAssumir) {
            log.warn("Usuário ID: {} tentou acessar o atendimento ID: {} sem ser participante.",
                    idUsuario, atendimento.getId());
            throw new RuntimeException("Voce nao participa deste atendimento!");
        }
    }

    private UsuarioModel getUsuario(Long idUsuario) {
        return this.usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> {
                    log.warn("Usuário com ID: {} não encontrado.", idUsuario);
                    return new UsuarioNaoEncontrado(
                            "Usuario com ID: " + idUsuario + " nao encontrado!");
                });
    }
}