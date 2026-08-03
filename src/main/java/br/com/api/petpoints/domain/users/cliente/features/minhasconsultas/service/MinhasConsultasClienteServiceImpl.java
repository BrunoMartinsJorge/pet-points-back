package br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.service;

import br.com.api.petpoints.domain.users.atendente.features.consultas.service.ConsultasAtendenteServiceImpl;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto.*;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.forms.ReagendamentoConsultaForm;
import br.com.api.petpoints.shared.exception.custom.IllegalAccessException;
import br.com.api.petpoints.shared.features.notificacoes.controller.NotificacoesController;
import br.com.api.petpoints.shared.features.notificacoes.form.NovaNotificacaoForm;
import br.com.api.petpoints.shared.features.payment.dto.MercadoPagoDto;
import br.com.api.petpoints.shared.features.payment.dto.PagamentoDto;
import br.com.api.petpoints.shared.features.payment.service.PagamentoService;
import br.com.api.petpoints.shared.form.AvaliacaoForm;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.forms.CancelarConsultaForm;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.forms.SolicitacaoConsultaForm;
import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.shared.enums.*;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.exception.custom.PerfilDesativadoException;
import br.com.api.petpoints.shared.features.logs.LogsServiceImpl;
import br.com.api.petpoints.shared.models.*;
import br.com.api.petpoints.shared.repository.*;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinhasConsultasClienteServiceImpl implements MinhasConsultasClienteService {

    private final UsuarioRepository usuarioRepository;
    private final ConsultaRepository consultaRepository;
    private final PetRepository petRepository;
    private final TipoConsultaRepository tipoConsultaRepository;
    private final LogsServiceImpl logsService;
    private final EspecializacaoRepository especializacaoRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final PagamentoService pagamentoService;
    private final NotificacoesController notificacoesController;

    private static final List<LocalTime> HORARIOS_FUNCIONAMENTO = List.of(
            LocalTime.of(8, 0), LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0),
            LocalTime.of(14, 0), LocalTime.of(15, 0), LocalTime.of(16, 0), LocalTime.of(17, 0),
            LocalTime.of(18, 0), LocalTime.of(19, 0), LocalTime.of(20, 0)
    );

    private UsuarioModel getUsuarioPorId(Long idUsuario) {
        return this.usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: " + idUsuario));
    }

    private TipoConsultaModel getTipoConsultaPorId(Long idTipoConsulta) {
        return this.tipoConsultaRepository.findById(idTipoConsulta).orElseThrow(() -> new ObjectNotFoundException("Tipo de Consulta com ID: " + idTipoConsulta));
    }

    private PetModel getPetPorId(Long idPet) {
        return this.petRepository.findById(idPet).orElseThrow(() -> new ObjectNotFoundException("Pet com ID: " + idPet + " não encontrado!"));
    }

    private ConsultaModel getConsultaPorId(Long idConsulta) {
        return this.consultaRepository.findById(idConsulta).orElseThrow(() -> new ObjectNotFoundException("Consulta com ID: " + idConsulta + " não encontrada!"));
    }

    @Override
    public InformacoesCardsConsultasClienteDto gerarInformacoesCards(Long idUsuario) {
        List<ConsultaModel> consultas = this.consultaRepository.findAllBySolicitante_Id(idUsuario);
        if (consultas.isEmpty()) return new InformacoesCardsConsultasClienteDto(0, 0, 0);
        long finalizadas = consultas.stream().filter(consulta -> consulta.getStatus().equals(StatusConsultaEnum.FINALIZADO)).count();
        long pendentes = consultas.stream().filter(consulta -> consulta.getStatus().equals(StatusConsultaEnum.PENDENTE)).count();
        long indeferidasCanceladas = consultas.stream().filter(consulta -> consulta.getStatus().equals(StatusConsultaEnum.CANCELADO) || consulta.getStatus().equals(StatusConsultaEnum.REPROVADA)).count();
        return new InformacoesCardsConsultasClienteDto(finalizadas, pendentes, indeferidasCanceladas);
    }

    @Override
    public List<MinhasConsultasDto> listarConsultasAprovadas(Long idUsuario) {
        return MinhasConsultasDto.convert(this.consultaRepository.buscarConsultasConfirmadasPorUsuario(idUsuario));
    }

    @Override
    public List<MinhasConsultasDto> listarMinhasConsultas(Long idUsuario) {
        Optional<UsuarioModel> usuario = usuarioRepository.findById(idUsuario);
        if (usuario.isEmpty()) throw new UsuarioNaoEncontrado("Usuário com ID " + idUsuario + " não encontrado!");
        if (usuario.get().getStatusPerfilEnum().equals(StatusPerfilEnum.D))
            throw new PerfilDesativadoException("Perfil com email " + usuario.get().getEmail() + " desabilitado!");
        List<ConsultaModel> minhasConsultas = this.consultaRepository.findAllBySolicitante_Id(idUsuario);
        return minhasConsultas.stream().map(MinhasConsultasDto::new).toList();
    }

    @Override
    public List<MinhasConsultasDto> listarConsultasPendentes(Long idUsuario) {
        return MinhasConsultasDto.convert(this.consultaRepository.findAllBySolicitante_IdAndStatus(idUsuario, StatusConsultaEnum.PENDENTE));
    }

    @Override
    public MinhasConsultasDto buscarProximaConsulta(Long idUsuario) {
        List<ConsultaModel> consultas = this.consultaRepository.findAllBySolicitante_IdAndStatus(idUsuario, StatusConsultaEnum.APROVADA)
                .stream().filter(consulta -> consulta.getDataConsulta().isAfter(LocalDateTime.now())).sorted(Comparator.comparing(ConsultaModel::getDataConsulta)).toList();
        if (consultas.isEmpty())
            return null;
        return new MinhasConsultasDto(consultas.getFirst());
    }

    @Override
    public MinhasConsultasDto buscarConsultaAtual(Long idUsuario) {
        List<ConsultaModel> consultas = this.consultaRepository.findAllBySolicitante_IdAndStatus(idUsuario, StatusConsultaEnum.INICIADO)
                .stream().sorted(Comparator.comparing(ConsultaModel::getDataConsulta)).toList();
        if (consultas.isEmpty())
            return null;
        return new MinhasConsultasDto(consultas.getFirst());
    }

    @Override
    public DetalhesConsultaSelecionadaDto buscarDetalhesConsulta(Long idConsulta) {
        ConsultaModel consulta = this.consultaRepository.findById(idConsulta).orElseThrow(() -> new ObjectNotFoundException("Consulta com ID: " + idConsulta + " não encontrada!"));
        return new DetalhesConsultaSelecionadaDto(consulta);
    }

    @Override
    @Transactional
    public void solicitarNovaConsulta(Long idUsuario, SolicitacaoConsultaForm form) {
        this.validarSolicitacaoDeConsulta(form);
        PetModel pet = this.getPetPorId(form.getIdPet());
        UsuarioModel solicitante = this.getUsuarioPorId(idUsuario);
        if (pet.getTutor() != solicitante) throw new RuntimeException("Esse Pet não é seu!");
        UsuarioModel veterinario = this.getUsuarioPorId(form.getIdVeterinario());
        TipoConsultaModel tipoConsulta = this.tipoConsultaRepository.findById(form.getIdTipoConsulta()).orElseThrow(() -> new ObjectNotFoundException("Tipo de consulta com ID: " + form.getIdTipoConsulta() + " não encontrado!"));
        ConsultaModel consulta = new ConsultaModel();
        consulta.setSolicitante(solicitante);
        consulta.setDataConsulta(form.getDataConsulta());
        consulta.setObservacoes(form.getObservacoes());
        consulta.setVeterinario(veterinario);
        consulta.setTipoConsulta(tipoConsulta);
        consulta.setPet(pet);
        consulta.setFormaPagamento(form.getFormaPagamento());
        this.consultaRepository.save(consulta);
    }

    @Override
    public void cancelarConsulta(Long idUsuario, CancelarConsultaForm form) {
        UsuarioModel cliente = this.getUsuarioPorId(idUsuario);
        ConsultaModel consulta = this.getConsultaPorId(form.getIdConsulta());
        if (consulta.getStatus() == StatusConsultaEnum.INICIADO || consulta.getStatus() == StatusConsultaEnum.FINALIZADO || consulta.getStatus() == StatusConsultaEnum.REPROVADA)
            throw new IllegalAccessException("A solicitação não pode mais ser cancelada!");
        consulta.setStatus(StatusConsultaEnum.CANCELADO);
        consulta.setMotivoCancelamento(form.getMotivoCancelamento());
        consulta.setCanceladoEm(LocalDateTime.now());
        this.consultaRepository.save(consulta);
        this.logsService.registrarLog(cliente, TipoLogEnum.CANCELOU_CONSULTA);
    }

    @Override
    public List<TiposConsultaDto> listarTiposConsulta() {
        List<TipoConsultaModel> tipos = this.tipoConsultaRepository.findAll().stream().filter(tipo -> !tipo.getVeterinarios().isEmpty()).toList();
        return TiposConsultaDto.convert(tipos);
    }

    @Override
    public List<VeterinariosTipoConsultaDto> listarVeterinariosTipoConsulta(Long idTipoConsulta) {
        TipoConsultaModel tipoConsulta = this.getTipoConsultaPorId(idTipoConsulta);
        List<VeterinariosTipoConsultaDto> dto = new ArrayList<>();
        for (UsuarioModel veterinario : tipoConsulta.getVeterinarios()) {
            List<ConsultaModel> avaliacoes = this.consultaRepository.findAllByVeterinario_Id(veterinario.getId());
            double avaliacao = 0;
            for (ConsultaModel consulta : avaliacoes) {
                if (consulta.getAvaliacao() == null) break;
                avaliacao += consulta.getAvaliacao().getPontuacao();
            }
            avaliacao = avaliacao / avaliacoes.size();
            List<EspecializacaoModel> especializacoes = this.especializacaoRepository.buscarPorVeterinario(veterinario);
            dto.add(new VeterinariosTipoConsultaDto(veterinario, especializacoes, avaliacao));
        }
        return dto;
    }

    @Override
    public List<DiaConsultasVeterinarioDto> buscarDiasHorariosDisponiveisVeterinario(Long idVeterinario) {
        Map<LocalDateTime, List<ConsultaModel>> consultas =
                this.consultaRepository.findAllByVeterinario_Id(idVeterinario)
                        .stream()
                        .filter(this::consultaValida)
                        .collect(Collectors.groupingBy(ConsultaModel::getDataConsulta));
        return consultas.entrySet().stream().map(value -> {
            LocalDate data = value.getKey().toLocalDate();
            List<LocalTime> horarios = value.getValue().stream().map(datas -> datas.getDataConsulta().toLocalTime()).toList();
            return new DiaConsultasVeterinarioDto(data, horarios);
        }).toList();
    }

    private boolean consultaValida(ConsultaModel consulta) {
        LocalDate dataConsulta = consulta.getDataConsulta().toLocalDate();
        StatusConsultaEnum status = consulta.getStatus();

        boolean dataValida =
                !dataConsulta.isAfter(LocalDate.now());

        boolean statusValido =
                status != StatusConsultaEnum.REPROVADA &&
                        status != StatusConsultaEnum.FINALIZADO &&
                        status != StatusConsultaEnum.CANCELADO;

        return dataValida || statusValido;
    }

    @Override
    public List<OpcoesPetConsultasDto> buscarPetsConsulta(Long idUsuario) {
        List<PetModel> pets = this.petRepository.findAllByTutor_Id(idUsuario);
        return OpcoesPetConsultasDto.convert(pets);
    }

    @Override
    public PagamentoConsultaDto buscarPagamentoConsulta(Long idConsulta) {
        ConsultaModel consulta = this.getConsultaPorId(idConsulta);
        if (consulta.getPagamento() == null) return null;
        if (!consulta.getPagamento().getTipoPagamento().equals(TipoPagamentoEnum.PIX)) {
            return new PagamentoConsultaDto(consulta.getPagamento());
        } else {
            try {
                MercadoPagoDto.OrderResponse informacoesPagamento = this.pagamentoService.buscarPagamentoPix(consulta.getPagamento().getId());
                PagamentoDto.PagamentoPixResponse respostaPix = this.pagamentoService.getPagamentoPixResponse(informacoesPagamento, consulta.getPagamento());
                return new PagamentoConsultaDto(consulta.getPagamento(), respostaPix);
            } catch (Exception e) {
                log.error("Ocorreu um erro ao buscar os detalhes do pagamento via PIX dessa consulta! {}", idConsulta);
            }
            return null;
        }
    }

    @Override
    @Transactional
    public void alterarFormaPagamentoConsulta(Long idUsuario, Long idConsulta, TipoPagamentoEnum formaPagamento) {
        UsuarioModel cliente = this.getUsuarioPorId(idUsuario);
        ConsultaModel consulta = this.getConsultaPorId(idConsulta);
        this.consultaPertenceCliente(consulta, cliente);

        if (consulta.getStatus() == StatusConsultaEnum.CANCELADO || consulta.getStatus() == StatusConsultaEnum.REPROVADA)
            throw new RuntimeException("A consulta não pode ser alterada devido seu estado atual!");

        PagamentoModel pagamento = consulta.getPagamento();

        if (pagamento == null) {
            consulta.setFormaPagamento(formaPagamento);
            if (formaPagamento == TipoPagamentoEnum.PIX)
                consulta.setPagamento(gerarCobrancaDaConsulta(consulta, consulta.getVeterinario()));
            this.consultaRepository.save(consulta);
            return;
        }

        boolean pixPagoEValido = pagamento.getTipoPagamento() == TipoPagamentoEnum.PIX
                && pagamento.getStatusPagamento() == StatusPagamentoEnum.APROVADO
                && (pagamento.getDataLimitePagamento() == null || pagamento.getDataLimitePagamento().isAfter(LocalDateTime.now()));

        boolean presencialJaAprovado = pagamento.getTipoPagamento() != TipoPagamentoEnum.PIX
                && pagamento.getStatusPagamento() == StatusPagamentoEnum.APROVADO;

        if (pixPagoEValido)
            throw new RuntimeException("O PIX já foi pago e ainda está válido, a forma de pagamento não pode ser alterada!");
        if (presencialJaAprovado)
            throw new RuntimeException("O pagamento já foi aprovado pelo atendente, a forma de pagamento não pode ser alterada!");

        if (pagamento.getTipoPagamento() == TipoPagamentoEnum.PIX) {
            this.pagamentoService.cancelarPagamentoPix(pagamento);
        }

        if (formaPagamento == TipoPagamentoEnum.PIX) {
            PagamentoDto.CriarPagamentoPixForm form = new PagamentoDto.CriarPagamentoPixForm(
                    pagamento.getValorPagamento(),
                    "Pagamento referente à consulta na clínica Pet Points do cliente " + consulta.getSolicitante().getNome(),
                    consulta.getSolicitante().getEmail(),
                    consulta.getSolicitante().getNome(),
                    "CONSULTA_ID_" + consulta.getId(),
                    consulta.getSolicitante().getCpf()
            );
            this.pagamentoService.gerarCobrancaPix(pagamento, form, consulta.getVeterinario());
        } else {
            this.pagamentoService.criarPagamentoPresencial(pagamento, formaPagamento, pagamento.getValorPagamento(), consulta.getVeterinario());
        }

        consulta.setFormaPagamento(formaPagamento);
        this.consultaRepository.save(consulta);
    }

    private PagamentoModel gerarCobrancaDaConsulta(ConsultaModel consulta, UsuarioModel veterinario) {
        TipoPagamentoEnum formaPagamento = consulta.getFormaPagamento() != null
                ? consulta.getFormaPagamento()
                : TipoPagamentoEnum.DINHEIRO;

        BigDecimal valor = BigDecimal.valueOf(consulta.getTipoConsulta().getValor());
        PagamentoModel pagamento = new PagamentoModel();

        if (formaPagamento == TipoPagamentoEnum.PIX) {
            PagamentoDto.CriarPagamentoPixForm formPagamento = new PagamentoDto.CriarPagamentoPixForm(
                    valor,
                    "Pagamento referente à consulta na clínica Pet Points do cliente " + consulta.getSolicitante().getNome(),
                    consulta.getSolicitante().getEmail(),
                    consulta.getSolicitante().getNome(),
                    "CONSULTA_ID_" + consulta.getId(),
                    consulta.getSolicitante().getCpf()
            );
            this.pagamentoService.gerarCobrancaPix(pagamento, formPagamento, veterinario);
            return pagamento;
        }

        // Cartão (sem integração online) ou dinheiro: pagamento presencial,
        // fica PENDENTE até o atendente confirmar o recebimento no balcão.
        return this.pagamentoService.criarPagamentoPresencial(pagamento, formaPagamento, valor, veterinario);
    }

    @Override
    public AvaliacaoConsultaDto buscarAvaliacaoPorConsulta(Long idUsuario, Long idConsulta) {
        UsuarioModel cliente = this.getUsuarioPorId(idUsuario);
        ConsultaModel consulta = this.getConsultaPorId(idConsulta);
        this.consultaPertenceCliente(consulta, cliente);
        AvaliacaoModel avaliacao = consulta.getAvaliacao();
        if (avaliacao == null) return new AvaliacaoConsultaDto();
        return new AvaliacaoConsultaDto(avaliacao);
    }

    @Override
    @Transactional
    public void avaliarConsulta(Long idUsuario, Long idConsulta, AvaliacaoForm form) {
        UsuarioModel cliente = this.getUsuarioPorId(idUsuario);
        ConsultaModel consulta = this.getConsultaPorId(idConsulta);
        this.consultaPertenceCliente(consulta, cliente);
        if (consulta.getAvaliacao() != null) throw new RuntimeException("Consulta já avaliada!");
        if (consulta.getStatus() != StatusConsultaEnum.FINALIZADO)
            throw new RuntimeException("A consulta só pode ser avaliada caso já esteja finalizada!");
        AvaliacaoModel avaliacao = this.avaliacaoRepository.save(new AvaliacaoModel(form, cliente));
        consulta.setAvaliacao(avaliacao);
        this.consultaRepository.save(consulta);
    }

    @Override
    public MinhasConsultasDto buscarConsultaPorId(Long idConsulta) {
        return new MinhasConsultasDto(this.getConsultaPorId(idConsulta));
    }

    @Override
    @Transactional
    public void reagendarConsulta(Long idUsuario, ReagendamentoConsultaForm form) {
        ConsultaModel consulta = this.getConsultaPorId(form.getIdConsulta());
        this.consultaPertenceCliente(consulta, this.getUsuarioPorId(idUsuario));
        if (!consulta.getStatus().equals(StatusConsultaEnum.PENDENTE) && !consulta.getStatus().equals(StatusConsultaEnum.APROVADA))
            throw new IllegalAccessException("O estado em que a consulta se encontra não pode receber reagendamentos!");
        if (!HORARIOS_FUNCIONAMENTO.contains(form.getDataConsulta().toLocalTime()))
            throw new IllegalAccessException("O horário informado para o reagendamento não consta na tabela de horários!");
        consulta.setDataConsulta(form.getDataConsulta());
        this.consultaRepository.save(consulta);
        this.notificarReagendamento(consulta);
    }

    private void consultaPertenceCliente(ConsultaModel consulta, UsuarioModel cliente) {
        if (!consulta.getSolicitante().equals(cliente))
            throw new RuntimeException("Você não pode acessar essa consulta!");
    }

    private void validarSolicitacaoDeConsulta(SolicitacaoConsultaForm form) {
        LocalDateTime dataSolicitada = form.getDataConsulta();
        List<ConsultaModel> consultasDoVeterinario =
                this.consultaRepository
                        .findAllByVeterinario_Id(form.getIdVeterinario())
                        .stream()
                        .filter(consulta -> consulta.getDataConsulta().toLocalDate().equals(dataSolicitada.toLocalDate()) && consulta.getDataConsulta().isAfter(dataSolicitada.minusHours(1)) && consulta.getDataConsulta().isBefore(dataSolicitada.plusHours(1))
                        )
                        .toList();
        if (!consultasDoVeterinario.isEmpty()) throw new RuntimeException("Já existe uma consulta nesse periodo!");
    }

    @Transactional
    protected void notificarReagendamento(ConsultaModel consulta) {
        NovaNotificacaoForm notificacao = new NovaNotificacaoForm(
                consulta.getSolicitante().getId(),
                "Reagendamento de Consulta",
                "Reagendamento de consulta EFETUADO",
                TiposNotificacoesEnum.CONSULTA
        );
        try {
            this.notificacoesController.enviarNotificacao(notificacao);
            log.info("Notificação de reagendamento de consulta enviada ao cliente!");
        } catch (Exception e) {
            log.error("Problema ao enviar notificação de reagendamento de consulta ao cliente!");
        }
        notificacao = new NovaNotificacaoForm(
                consulta.getVeterinario().getId(),
                "Consulta Reagendada",
                "Uma consulta com " + consulta.getSolicitante().getNome() + " em " + LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getDataConsulta()) + " foi reagendada!",
                TiposNotificacoesEnum.CONSULTA
        );
        try {
            this.notificacoesController.enviarNotificacao(notificacao);
            log.info("Notificação de reagendamento de consulta enviada ao veterinário!");
        } catch (Exception e) {
            log.error("Problema ao enviar notificação de reagendamento de consulta ao veterinário!");
        }
        notificacao = new NovaNotificacaoForm(
                consulta.getVeterinario().getId(),
                "Consulta Reagendada",
                "Uma consulta com " + consulta.getSolicitante().getNome() + " e Dr(a) " + consulta.getVeterinario().getNome() + " em " + LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getDataConsulta()) + " foi reagendada!",
                TiposNotificacoesEnum.CONSULTA
        );
        try {
            this.notificacoesController.enviarNotificacao(notificacao);
            log.info("Notificação de reagendamento de consulta enviada ao atendente!");
        } catch (Exception e) {
            log.error("Problema ao enviar notificação de reagendamento de consulta ao atendente!");
        }
    }
}
