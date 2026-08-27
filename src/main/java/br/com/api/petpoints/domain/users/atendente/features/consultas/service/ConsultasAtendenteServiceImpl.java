package br.com.api.petpoints.domain.users.atendente.features.consultas.service;

import br.com.api.petpoints.domain.users.atendente.features.consultas.dto.AvaliacaoConsultaDto;
import br.com.api.petpoints.domain.users.atendente.features.consultas.dto.ConsultasAtendenteDto;
import br.com.api.petpoints.domain.users.atendente.features.consultas.dto.InformacoesPagamentoDto;
import br.com.api.petpoints.domain.users.atendente.features.consultas.dto.OpcaoClienteConsultaDto;
import br.com.api.petpoints.domain.users.atendente.features.consultas.dto.PendenciaPagamentoClienteDto;
import br.com.api.petpoints.domain.users.atendente.features.consultas.dto.PendenciasFinanceirasClienteDto;
import br.com.api.petpoints.domain.users.atendente.features.consultas.forms.IndeferirConsultaForm;
import br.com.api.petpoints.domain.users.atendente.features.consultas.forms.RegistroConsultaAtendenteForm;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto.DiaConsultasVeterinarioDto;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto.OpcoesPetConsultasDto;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto.TiposConsultaDto;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto.VeterinariosTipoConsultaDto;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.service.MinhasConsultasClienteServiceImpl;
import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.enums.StatusPerfilEnum;
import br.com.api.petpoints.shared.enums.TipoLogEnum;
import br.com.api.petpoints.shared.enums.TiposNotificacoesEnum;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.features.logs.LogsServiceImpl;
import br.com.api.petpoints.shared.features.notificacoes.controller.NotificacoesController;
import br.com.api.petpoints.shared.features.notificacoes.form.NovaNotificacaoForm;
import br.com.api.petpoints.shared.models.ConsultaModel;
import br.com.api.petpoints.shared.models.PetModel;
import br.com.api.petpoints.shared.models.TipoConsultaModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import br.com.api.petpoints.shared.repository.PetRepository;
import br.com.api.petpoints.shared.repository.TipoConsultaRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsultasAtendenteServiceImpl implements ConsultasAtendenteService {

    private final UsuarioRepository usuarioRepository;
    private final ConsultaRepository consultaRepository;
    private final PetRepository petRepository;
    private final TipoConsultaRepository tipoConsultaRepository;
    private final LogsServiceImpl logsService;
    private final NotificacoesController notificacoesController;
    private final MinhasConsultasClienteServiceImpl minhasConsultasClienteService;

    private static final List<LocalTime> HORARIOS_FUNCIONAMENTO = List.of(
            LocalTime.of(8, 0), LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0),
            LocalTime.of(14, 0), LocalTime.of(15, 0), LocalTime.of(16, 0), LocalTime.of(17, 0),
            LocalTime.of(18, 0), LocalTime.of(19, 0), LocalTime.of(20, 0)
    );

    // Quantos dias à frente procurar antes de desistir do reagendamento.
    private static final int LIMITE_DIAS_REAGENDAMENTO = 60;

    private ConsultaModel getConsultaPorId(Long idConsulta) {
        log.info("Busca de consulta em andamento...");
        return consultaRepository.findById(idConsulta).orElseThrow(() -> new ObjectNotFoundException("Consulta com ID: " + idConsulta + " não encontrada!"));
    }

    private UsuarioModel getUsuarioPorId(Long idUsuario) {
        log.info("Busca de usuário em andamento...");
        return usuarioRepository.findById(idUsuario).orElseThrow(() -> new ObjectNotFoundException("Usuário com ID: " + idUsuario + " não encontrado!"));
    }

    @Override
    public List<ConsultasAtendenteDto> listarConsultasPendentes() {
        List<ConsultaModel> consultas = this.consultaRepository.findAllByStatus(StatusConsultaEnum.PENDENTE);
        return consultas.stream().map(ConsultasAtendenteDto::new).toList();
    }

    @Override
    public List<ConsultasAtendenteDto> listarHistoricoDeConsultas() {
        List<ConsultaModel> consultas = this.consultaRepository.findAll();
        return consultas.stream().map(ConsultasAtendenteDto::new).toList();
    }

    @Override
    @Transactional
    public void deferirSolicitacaoDeConsulta(Long idConsulta, Long idUsuario) {
        ConsultaModel consulta = this.getConsultaPorId(idConsulta);
        UsuarioModel atendente = this.getUsuarioPorId(idUsuario);
        if (consulta.getStatus() != StatusConsultaEnum.PENDENTE)
            throw new RuntimeException("Consulta já deferida!");

        log.info("Processando aprovação de consulta...");

        // Se o horário original já passou (atraso no deferimento), tentamos reagendar ANTES
        // de aprovar. Só seguimos para APROVADA se conseguimos uma nova data.
        if (consulta.getDataConsulta().isBefore(LocalDateTime.now())) {
            Optional<LocalDateTime> novaData = this.buscarProximaDataConsulta(consulta);

            if (novaData.isEmpty()) {
                // Não há mais nenhum horário disponível dentro do limite: reprova por atraso
                // e avisa o cliente. Nada de exceção aqui, para que o estado fique persistido
                // de forma consistente (a transação commita normalmente).
                log.warn("Nenhum horário disponível para reagendar a consulta {}. Reprovando por atraso.", idConsulta);
                consulta.setStatus(StatusConsultaEnum.REPROVADA);
                consulta.setAtendente(atendente);
                consulta.setDeferidoEm(LocalDateTime.now());
                consulta.setMotivoIndeferimento("Atraso no deferimento: não há horários disponíveis para reagendamento.");
                this.logsService.registrarLog(atendente, TipoLogEnum.INDEFERIU_CONSULTA);
                consulta = this.consultaRepository.save(consulta);
                this.notificarReagendamentoImpossivel(consulta, idUsuario);
                return;
            }

            consulta.setDataConsulta(novaData.get());
        }

        consulta.setStatus(StatusConsultaEnum.APROVADA);
        consulta.setAtendente(atendente);
        consulta.setDeferidoEm(LocalDateTime.now());
        this.logsService.registrarLog(atendente, TipoLogEnum.DEFERIU_CONSULTA);

        consulta = consultaRepository.save(consulta);
        this.enviarNotificacaoClienteVeterinario(consulta);
        log.debug("Aprovação de consulta concluida!");
    }

    /**
     * Procura o próximo horário livre para o veterinário da consulta.
     * Regra: mesmo dia, primeiro horário DEPOIS do original; se não houver,
     * o horário disponível mais próximo ANTES; se o dia inteiro estiver cheio,
     * avança dia a dia pegando o primeiro horário livre.
     * Retorna vazio se nada for encontrado dentro do limite.
     */
    private Optional<LocalDateTime> buscarProximaDataConsulta(ConsultaModel consulta) {
        Map<LocalDate, List<LocalTime>> ocupadosPorDia =
                this.consultaRepository.findAllByVeterinario_Id(consulta.getVeterinario().getId())
                        .stream()
                        .filter(c -> !c.getId().equals(consulta.getId())) // ignora a própria consulta
                        .filter(this::consultaOcupaHorario)
                        .collect(Collectors.groupingBy(
                                c -> c.getDataConsulta().toLocalDate(),
                                Collectors.mapping(
                                        c -> c.getDataConsulta().toLocalTime().truncatedTo(ChronoUnit.HOURS),
                                        Collectors.toList()
                                )
                        ));

        LocalDate diaOriginal = consulta.getDataConsulta().toLocalDate();
        LocalTime horaOriginal = consulta.getDataConsulta().toLocalTime();

        List<LocalTime> disponiveisDia =
                horariosDisponiveis(diaOriginal, ocupadosPorDia.getOrDefault(diaOriginal, List.of()));

        // 1) Primeiro horário disponível DEPOIS do original (no mesmo dia).
        Optional<LocalTime> posterior = disponiveisDia.stream()
                .filter(h -> h.isAfter(horaOriginal))
                .findFirst();
        if (posterior.isPresent())
            return Optional.of(LocalDateTime.of(diaOriginal, posterior.get()));

        // 2) Não havendo depois, o disponível mais próximo ANTES (o último anterior).
        Optional<LocalTime> anterior = disponiveisDia.stream()
                .filter(h -> h.isBefore(horaOriginal))
                .reduce((primeiro, segundo) -> segundo);
        if (anterior.isPresent())
            return Optional.of(LocalDateTime.of(diaOriginal, anterior.get()));

        // 3) Mesmo dia cheio: avança dia a dia pegando o primeiro horário livre.
        for (int i = 1; i <= LIMITE_DIAS_REAGENDAMENTO; i++) {
            LocalDate dia = diaOriginal.plusDays(i);
            List<LocalTime> disponiveis =
                    horariosDisponiveis(dia, ocupadosPorDia.getOrDefault(dia, List.of()));
            if (!disponiveis.isEmpty())
                return Optional.of(LocalDateTime.of(dia, disponiveis.getFirst()));
        }

        return Optional.empty();
    }

    /** Horários de funcionamento que não estão ocupados e não estão no passado. */
    private List<LocalTime> horariosDisponiveis(LocalDate dia, List<LocalTime> ocupados) {
        LocalDateTime agora = LocalDateTime.now();
        return HORARIOS_FUNCIONAMENTO.stream()
                .filter(h -> !ocupados.contains(h))                    // remove os ocupados
                .filter(h -> LocalDateTime.of(dia, h).isAfter(agora))  // nunca agenda no passado
                .sorted()
                .toList();
    }

    /**
     * Um horário só está "ocupado" se a consulta está viva (pendente ou aprovada).
     * Consultas reprovadas/finalizadas/canceladas liberam o slot.
     */
    private boolean consultaOcupaHorario(ConsultaModel consulta) {
        StatusConsultaEnum status = consulta.getStatus();
        return status == StatusConsultaEnum.PENDENTE
                || status == StatusConsultaEnum.APROVADA;
    }

    /** Avisa o cliente que o reagendamento não foi possível; em caso de falha, cai para o atendente. */
    private void notificarReagendamentoImpossivel(ConsultaModel consulta, Long idAtendente) {
        NovaNotificacaoForm notificacao = new NovaNotificacaoForm(
                consulta.getSolicitante().getId(),
                "Horários Indisponíveis!",
                "Olá, " + consulta.getSolicitante().getNome() + ", sentimos muito em informar que a sua solicitação de consulta com o Dr(a) "
                        + consulta.getVeterinario().getNome() + " no dia e hora "
                        + LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getDataConsulta())
                        + " não pôde ocorrer devido ao atraso no deferimento da mesma. Por favor, solicite uma nova em outro período.",
                TiposNotificacoesEnum.CONSULTA
        );
        try {
            this.notificacoesController.enviarNotificacao(notificacao);
            log.info("Cliente notificado sobre a impossibilidade de reagendamento.");
        } catch (Exception e) {
            log.error("Não foi possível notificar o cliente, iniciando processo de notificar atendente...");
            notificacao.setIdDestinatario(idAtendente);
            notificacao.setTitulo("Avisar Cliente");
            notificacao.setMensagem("Por favor, avisar o cliente " + consulta.getSolicitante().getNome()
                    + " que a sua consulta com o Dr(a) " + consulta.getVeterinario().getNome() + " no dia e hora "
                    + LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getDataConsulta())
                    + " não pôde ocorrer devido ao atraso no deferimento da mesma. Solicite que ele faça outra solicitação!");
            try {
                this.notificacoesController.enviarNotificacao(notificacao); // <- envio que faltava
                log.info("Atendente notificado para avisar o cliente manualmente.");
            } catch (Exception ex) {
                log.error("Também não foi possível notificar o atendente sobre o reagendamento impossível.");
            }
        }
    }

    @Override
    @Transactional
    public void indeferirSolicitacaoDeConsulta(IndeferirConsultaForm form, Long idUsuario) {
        ConsultaModel consulta = this.getConsultaPorId(form.getIdConsulta());
        UsuarioModel atendente = this.getUsuarioPorId(idUsuario);
        if (consulta.getStatus() != StatusConsultaEnum.PENDENTE) throw new RuntimeException("Consulta já deferida!");
        log.info("Processando indeferimento de consulta...");
        consulta.setStatus(StatusConsultaEnum.REPROVADA);
        consulta.setAtendente(atendente);
        consulta.setDeferidoEm(LocalDateTime.now());
        consulta.setMotivoIndeferimento(form.getMotivo());
        this.logsService.registrarLog(atendente, TipoLogEnum.INDEFERIU_CONSULTA);
        consulta = consultaRepository.save(consulta);
        this.enviarNotificacaoClienteVeterinario(consulta);
        log.debug("Indeferimento de consulta concluida!");
    }

    public List<ConsultasAtendenteDto> listarConsultasComPagamentosPendentesDoCliente(Long idCliente) {
        return this.consultaRepository.findAllBySolicitante_IdAndPagamentoIsNull(idCliente).stream().filter(consulta -> consulta.getStatus() == StatusConsultaEnum.FINALIZADO).map(ConsultasAtendenteDto::new).toList();
    }

    /**
     * Levanta as cobranças em aberto do cliente para que o atendente veja, antes
     * de aprovar uma nova solicitação, se existe alguma pendência (ou atraso) na
     * clínica. É apenas informativo — a avaliação da cobrança em si acontece na
     * tela de Pagamentos da Clínica.
     */
    @Override
    public PendenciasFinanceirasClienteDto buscarPendenciasFinanceirasDoCliente(Long idCliente) {
        UsuarioModel cliente = this.getUsuarioPorId(idCliente);
        LocalDateTime agora = LocalDateTime.now();

        List<PendenciaPagamentoClienteDto> pendencias = this.consultaRepository
                .buscarConsultasComPagamentoPendenteDoCliente(idCliente)
                .stream()
                .map(consulta -> new PendenciaPagamentoClienteDto(consulta, agora))
                .sorted(Comparator.comparingLong(PendenciaPagamentoClienteDto::getDiasEmAtraso).reversed())
                .toList();

        List<PendenciaPagamentoClienteDto> atrasadas = pendencias.stream()
                .filter(PendenciaPagamentoClienteDto::isAtrasado)
                .toList();

        return new PendenciasFinanceirasClienteDto(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                pendencias.size(),
                atrasadas.size(),
                this.somarValores(pendencias),
                this.somarValores(atrasadas),
                pendencias
        );
    }

    private BigDecimal somarValores(List<PendenciaPagamentoClienteDto> pendencias) {
        return pendencias.stream()
                .map(PendenciaPagamentoClienteDto::getValor)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public InformacoesPagamentoDto buscarInformacoesPagamento(Long idConsulta) {
        ConsultaModel consulta = this.getConsultaPorId(idConsulta);
        if (consulta.getPagamento() == null) return null;
        return new InformacoesPagamentoDto(consulta.getPagamento());
    }

    @Override
    public AvaliacaoConsultaDto buscarAvaliacao(Long idConsulta) {
        ConsultaModel consulta = this.getConsultaPorId(idConsulta);
        if (consulta.getAvaliacao() == null) return new AvaliacaoConsultaDto();
        return new AvaliacaoConsultaDto(consulta.getAvaliacao());
    }

    @Transactional
    protected void enviarNotificacaoClienteVeterinario(ConsultaModel consulta) {
        if (consulta.getStatus().equals(StatusConsultaEnum.APROVADA)) {
            String dataFormatada = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getDataConsulta());

            NovaNotificacaoForm notificacao = new NovaNotificacaoForm(
                    consulta.getSolicitante().getId(),
                    "Solicitação de Consulta",
                    "Solicitação de consulta APROVADA para " + dataFormatada,
                    TiposNotificacoesEnum.CONSULTA
            );
            try {
                this.notificacoesController.enviarNotificacao(notificacao);
                log.info("Notificação de aprovação de consulta enviada ao cliente!");
            } catch (Exception e) {
                log.error("Problema ao enviar notificação de aprovação de consulta ao cliente!");
            }
            notificacao = new NovaNotificacaoForm(
                    consulta.getVeterinario().getId(),
                    "Consulta Agendada",
                    "Uma nova consulta foi agendada para " + dataFormatada + "!",
                    TiposNotificacoesEnum.CONSULTA
            );
            try {
                this.notificacoesController.enviarNotificacao(notificacao);
                log.info("Notificação de aprovação de consulta enviada ao veterinário!");
            } catch (Exception e) {
                log.error("Problema ao enviar notificação de aprovação de consulta ao veterinário!");
            }
        } else if (consulta.getStatus().equals(StatusConsultaEnum.REPROVADA)) {
            NovaNotificacaoForm notificacao = new NovaNotificacaoForm(
                    consulta.getSolicitante().getId(),
                    "Solicitação de Consulta",
                    "Solicitação de consulta REPROVADA",
                    TiposNotificacoesEnum.CONSULTA
            );
            try {
                this.notificacoesController.enviarNotificacao(notificacao);
                log.info("Notificação de indeferimento de consulta enviada ao cliente!");
            } catch (Exception e) {
                log.error("Problema ao enviar notificação de indeferimento de consulta ao cliente!");
            }
        }
    }

    @Override
    public ConsultasAtendenteDto buscarConsultaPorId(Long idUsuario, Long idConsulta) {
        UsuarioModel atendente = this.getUsuarioPorId(idUsuario);
        ConsultaModel consulta = this.getConsultaPorId(idConsulta);
        if (!consulta.getAtendente().equals(atendente))
            throw new RuntimeException("A consulta não esta ligada ao atendente!");
        return new ConsultasAtendenteDto(consulta);
    }

    // ------------------------------------------------------------------
    // Registro direto de consulta pelo atendente
    // ------------------------------------------------------------------

    @Override
    public List<OpcaoClienteConsultaDto> listarClientesParaRegistro() {
        List<UsuarioModel> clientes = this.usuarioRepository.findAllByPermissao(TipoUsuario.C)
                .stream()
                .filter(cliente -> cliente.getStatusPerfilEnum() == StatusPerfilEnum.A)
                .toList();
        return OpcaoClienteConsultaDto.convert(clientes);
    }

    @Override
    public List<OpcoesPetConsultasDto> listarPetsDoCliente(Long idCliente) {
        UsuarioModel cliente = this.getUsuarioPorId(idCliente);
        if (cliente.getPermissao() != TipoUsuario.C)
            throw new RuntimeException("O usuário informado não é um cliente!");
        return this.minhasConsultasClienteService.buscarPetsConsulta(idCliente);
    }

    @Override
    public List<TiposConsultaDto> listarTiposConsultaParaRegistro() {
        return this.minhasConsultasClienteService.listarTiposConsulta();
    }

    @Override
    public List<VeterinariosTipoConsultaDto> listarVeterinariosTipoConsulta(Long idTipoConsulta) {
        return this.minhasConsultasClienteService.listarVeterinariosTipoConsulta(idTipoConsulta);
    }

    @Override
    public List<DiaConsultasVeterinarioDto> buscarHorariosVeterinario(Long idVeterinario) {
        return this.minhasConsultasClienteService.buscarDiasHorariosDisponiveisVeterinario(idVeterinario);
    }

    /**
     * Registra uma consulta diretamente pelo atendente. Diferente do fluxo do cliente,
     * não existe etapa de solicitação/deferimento: a consulta já é criada APROVADA,
     * com o atendente responsável e a data de deferimento preenchidos.
     */
    @Override
    @Transactional
    public void registrarConsulta(RegistroConsultaAtendenteForm form, Long idAtendente) {
        UsuarioModel atendente = this.getUsuarioPorId(idAtendente);
        UsuarioModel cliente = this.getUsuarioPorId(form.getIdCliente());
        UsuarioModel veterinario = this.getUsuarioPorId(form.getIdVeterinario());

        if (cliente.getPermissao() != TipoUsuario.C)
            throw new RuntimeException("O usuário informado como cliente não é um cliente!");
        if (cliente.getStatusPerfilEnum() == StatusPerfilEnum.D)
            throw new RuntimeException("O perfil do cliente informado está desativado!");
        if (veterinario.getPermissao() != TipoUsuario.V)
            throw new RuntimeException("O usuário informado como veterinário não é um veterinário!");

        PetModel pet = this.petRepository.findById(form.getIdPet())
                .orElseThrow(() -> new ObjectNotFoundException("Pet com ID: " + form.getIdPet() + " não encontrado!"));
        if (!pet.getTutor().getId().equals(cliente.getId()))
            throw new RuntimeException("O pet informado não pertence ao cliente selecionado!");

        TipoConsultaModel tipoConsulta = this.tipoConsultaRepository.findById(form.getIdTipoConsulta())
                .orElseThrow(() -> new ObjectNotFoundException("Tipo de consulta com ID: " + form.getIdTipoConsulta() + " não encontrado!"));
        boolean veterinarioAtendeTipo = tipoConsulta.getVeterinarios() != null
                && tipoConsulta.getVeterinarios().stream()
                .anyMatch(vet -> vet.getId().equals(veterinario.getId()));
        if (!veterinarioAtendeTipo)
            throw new RuntimeException("O veterinário selecionado não atende esse tipo de consulta!");

        this.validarDataConsultaRegistro(form.getDataConsulta(), veterinario.getId());

        log.info("Registrando consulta diretamente pelo atendente {}...", atendente.getId());

        ConsultaModel consulta = new ConsultaModel();
        consulta.setSolicitante(cliente);
        consulta.setPet(pet);
        consulta.setVeterinario(veterinario);
        consulta.setTipoConsulta(tipoConsulta);
        consulta.setDataConsulta(form.getDataConsulta());
        consulta.setObservacoes(form.getObservacoes());
        consulta.setFormaPagamento(form.getFormaPagamento());
        consulta.setStatus(StatusConsultaEnum.APROVADA);
        consulta.setAtendente(atendente);
        consulta.setDeferidoEm(LocalDateTime.now());

        consulta = this.consultaRepository.save(consulta);
        this.logsService.registrarLog(atendente, TipoLogEnum.DEFERIU_CONSULTA);
        this.notificarRegistroConsulta(consulta);
        log.debug("Registro de consulta pelo atendente concluído!");
    }

    /**
     * Valida o horário escolhido: precisa estar na tabela de funcionamento, não pode
     * estar no passado e o veterinário não pode ter outra consulta viva no período.
     */
    private void validarDataConsultaRegistro(LocalDateTime dataConsulta, Long idVeterinario) {
        if (!HORARIOS_FUNCIONAMENTO.contains(dataConsulta.toLocalTime()))
            throw new RuntimeException("O horário informado não consta na tabela de horários de funcionamento!");

        if (dataConsulta.isBefore(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)))
            throw new RuntimeException("Não é possível registrar uma consulta em um horário que já passou!");

        boolean horarioOcupado = this.consultaRepository.findAllByVeterinario_Id(idVeterinario)
                .stream()
                .filter(this::consultaOcupaHorario)
                .anyMatch(consulta -> consulta.getDataConsulta()
                        .truncatedTo(ChronoUnit.HOURS)
                        .isEqual(dataConsulta.truncatedTo(ChronoUnit.HOURS)));

        if (horarioOcupado)
            throw new RuntimeException("O veterinário já possui uma consulta nesse horário!");
    }

    /** Avisa cliente e veterinário que a consulta foi registrada pela clínica. */
    @Transactional
    protected void notificarRegistroConsulta(ConsultaModel consulta) {
        String dataFormatada = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getDataConsulta());

        NovaNotificacaoForm notificacao = new NovaNotificacaoForm(
                consulta.getSolicitante().getId(),
                "Consulta Registrada",
                "A clínica registrou uma consulta para você com o Dr(a) " + consulta.getVeterinario().getNome()
                        + " em " + dataFormatada + ".",
                TiposNotificacoesEnum.CONSULTA
        );
        try {
            this.notificacoesController.enviarNotificacao(notificacao);
            log.info("Notificação de registro de consulta enviada ao cliente!");
        } catch (Exception e) {
            log.error("Problema ao enviar notificação de registro de consulta ao cliente!");
        }

        notificacao = new NovaNotificacaoForm(
                consulta.getVeterinario().getId(),
                "Consulta Agendada",
                "Uma nova consulta com " + consulta.getSolicitante().getNome() + " foi registrada para "
                        + dataFormatada + "!",
                TiposNotificacoesEnum.CONSULTA
        );
        try {
            this.notificacoesController.enviarNotificacao(notificacao);
            log.info("Notificação de registro de consulta enviada ao veterinário!");
        } catch (Exception e) {
            log.error("Problema ao enviar notificação de registro de consulta ao veterinário!");
        }
    }
}