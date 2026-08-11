package br.com.api.petpoints.domain.users.atendente.features.pagamentos.service;

import br.com.api.petpoints.domain.users.atendente.features.pagamentos.dto.CardsPagamentosClinica;
import br.com.api.petpoints.domain.users.atendente.features.pagamentos.dto.DetalhesPagamentoClinicaDto;
import br.com.api.petpoints.domain.users.atendente.features.pagamentos.dto.EventoPagamentoDto;
import br.com.api.petpoints.domain.users.atendente.features.pagamentos.dto.PagamentosClinicaDto;
import br.com.api.petpoints.domain.users.atendente.features.pagamentos.dto.ResponsavelPagamentoDto;
import br.com.api.petpoints.domain.users.atendente.features.pagamentos.dto.TransacaoPagamentoDto;
import br.com.api.petpoints.domain.users.atendente.features.pagamentos.forms.IndeferirPagamentoClinicaForm;
import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import br.com.api.petpoints.shared.enums.TipoLogEnum;
import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import br.com.api.petpoints.shared.enums.TiposNotificacoesEnum;
import br.com.api.petpoints.shared.exception.custom.IllegalAccessException;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.features.logs.LogsServiceImpl;
import br.com.api.petpoints.shared.features.notificacoes.controller.NotificacoesController;
import br.com.api.petpoints.shared.features.notificacoes.form.NovaNotificacaoForm;
import br.com.api.petpoints.shared.features.payment.dto.MercadoPagoDto;
import br.com.api.petpoints.shared.features.payment.service.PagamentoService;
import br.com.api.petpoints.shared.models.PagamentoModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import br.com.api.petpoints.shared.repository.PagamentoRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagamentosClinicaAtendenteServiceImpl implements PagamentosClinicaAtendenteService {

    private final PagamentoRepository pagamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConsultaRepository consultaRepository;
    private final LogsServiceImpl logsService;
    private final PagamentoService pagamentoService;
    private final NotificacoesController notificacoesController;

    private UsuarioModel getUsuarioPorId(Long id) {
        return this.usuarioRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Atendente com ID: " + id + " não encontrado!"));
    }

    private PagamentoModel getPagamentoPorId(Long id) {
        return this.pagamentoRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Pagamento com ID: " + id + " não encontrado!"));
    }

    @Override
    public CardsPagamentosClinica buscarCardsPagamentoClinica(Long idUsuario) {
        UsuarioModel atendente = this.getUsuarioPorId(idUsuario);
        List<PagamentoModel> pagamentos = this.pagamentoRepository.findAll();
        Long totalPagamentosClinica = (long) pagamentos.size();
        Long meusPagamentosClinica = (long) pagamentos.stream().filter(pagamento -> pagamento.getAprovadoPor() != null && pagamento.getAprovadoPor() == atendente).toList().size();
        Long pagamentosPendentesAtrasados = (long) pagamentos.stream().filter(pagamento -> pagamento.getStatusPagamento() == StatusPagamentoEnum.PENDENTE).toList().size();
        return new CardsPagamentosClinica(totalPagamentosClinica, meusPagamentosClinica, pagamentosPendentesAtrasados);
    }

    @Override
    public List<PagamentosClinicaDto> buscarHistoricoPagamentosClinica() {
        List<PagamentoModel> pagamentos = this.pagamentoRepository.findAll();
        return PagamentosClinicaDto.convert(pagamentos);
    }

    @Override
    public List<PagamentosClinicaDto> buscarPagamentosPendentesAtrasados() {
        List<PagamentoModel> pagamentos = this.pagamentoRepository.buscarPagamentosPendentesAtrasados(LocalDateTime.now());
        return PagamentosClinicaDto.convert(pagamentos);
    }

    @Override
    @Transactional
    public void registrarPagamento(Long idUsuario, Long idPagamento) {
        UsuarioModel atendente = this.getUsuarioPorId(idUsuario);
        PagamentoModel pagamento = this.getPagamentoPorId(idPagamento);
        if (!pagamento.getTipoPagamento().equals(TipoPagamentoEnum.DINHEIRO) && pagamento.getStatusPagamento() != StatusPagamentoEnum.PENDENTE)
            throw new IllegalAccessException("A forma de pagamento não permite que o atendente em questão 'marque' como pago!");
        pagamento.setStatusPagamento(StatusPagamentoEnum.APROVADO);
        pagamento.setAprovadoPor(atendente);
        pagamento.setDataAtualizacao(LocalDateTime.now());
        pagamento.setDataPagamento(LocalDateTime.now());
        this.pagamentoRepository.save(pagamento);
        this.logsService.registrarLog(atendente, TipoLogEnum.REGISTROU_PAGAMENTO_PRESENCIAL, " Valor do pagamento R$" + pagamento.getValorPagamento());
    }

    @Override
    public DetalhesPagamentoClinicaDto buscarDetalhesPagamento(Long idPagamento) {
        return this.montarDetalhes(this.getPagamentoPorId(idPagamento), null);
    }

    @Override
    @Transactional
    public DetalhesPagamentoClinicaDto consultarStatusTransacao(Long idPagamento) {
        PagamentoModel pagamento = this.getPagamentoPorId(idPagamento);
        if (pagamento.getIdPagamentoExterno() == null)
            throw new IllegalAccessException("Esse pagamento não possui uma transação no gateway para ser consultada!");
        MercadoPagoDto.OrderResponse order = this.pagamentoService.sincronizarComGateway(pagamento);
        return this.montarDetalhes(pagamento, order);
    }

    @Override
    @Transactional
    public void indeferirPagamento(Long idUsuario, Long idPagamento, IndeferirPagamentoClinicaForm form) {
        UsuarioModel atendente = this.getUsuarioPorId(idUsuario);
        PagamentoModel pagamento = this.getPagamentoPorId(idPagamento);

        if (form == null || form.getMotivoIndeferimento() == null || form.getMotivoIndeferimento().isBlank())
            throw new IllegalAccessException("Informe o motivo do indeferimento do pagamento!");
        if (pagamento.getTipoPagamento() == TipoPagamentoEnum.PIX)
            throw new IllegalAccessException("Pagamentos via PIX são confirmados automaticamente pelo Mercado Pago e não podem ser indeferidos manualmente!");
        if (pagamento.getStatusPagamento() == StatusPagamentoEnum.APROVADO)
            throw new IllegalAccessException("Esse pagamento já foi aprovado e não pode ser indeferido!");

        pagamento.setStatusPagamento(StatusPagamentoEnum.REPROVADO);
        pagamento.setMotivoIndeferimento(form.getMotivoIndeferimento().trim());
        pagamento.setAprovadoPor(atendente);
        pagamento.setDataAtualizacao(LocalDateTime.now());
        this.pagamentoRepository.save(pagamento);

        this.notificarClienteIndeferimento(pagamento);
        this.logsService.registrarLog(atendente, TipoLogEnum.INDEFERIU_PAGAMENTO, " Valor do pagamento R$" + pagamento.getValorPagamento());
    }

    private DetalhesPagamentoClinicaDto montarDetalhes(PagamentoModel pagamento, MercadoPagoDto.OrderResponse order) {
        DetalhesPagamentoClinicaDto detalhes = new DetalhesPagamentoClinicaDto(pagamento);

        if (pagamento.getEmitidoPor() != null)
            detalhes.setEmitidoPor(new ResponsavelPagamentoDto(pagamento.getEmitidoPor(), pagamento.getDataCriacao()));

        if (pagamento.getAprovadoPor() != null) {
            LocalDateTime dataAvaliacao = pagamento.getDataPagamento() != null
                    ? pagamento.getDataPagamento()
                    : pagamento.getDataAtualizacao();
            detalhes.setAprovadoPor(new ResponsavelPagamentoDto(pagamento.getAprovadoPor(), dataAvaliacao));
        }

        detalhes.setTransacao(this.montarTransacao(pagamento, order));
        detalhes.setHistorico(this.montarHistorico(pagamento));
        return detalhes;
    }

    /**
     * A transação só existe para pagamentos que passaram pelo gateway. Ao abrir
     * os detalhes, a ordem ainda não foi consultada ({@code order} nulo) e são
     * devolvidos somente os dados já persistidos localmente — a consulta ao
     * Mercado Pago acontece sob demanda, pelo botão "Consultar status".
     */
    private TransacaoPagamentoDto montarTransacao(PagamentoModel pagamento, MercadoPagoDto.OrderResponse order) {
        if (pagamento.getIdPagamentoExterno() == null) return null;
        TransacaoPagamentoDto transacao = new TransacaoPagamentoDto(pagamento);
        transacao.preencherComOrdem(order);
        return transacao;
    }

    /**
     * O sistema não mantém uma tabela de eventos de pagamento, então a linha do
     * tempo é derivada do próprio ciclo de vida do registro (emissão, baixa,
     * indeferimento e estados finais informados pelo gateway).
     */
    private List<EventoPagamentoDto> montarHistorico(PagamentoModel pagamento) {
        List<EventoPagamentoDto> historico = new ArrayList<>();
        String emitente = pagamento.getEmitidoPor() != null ? pagamento.getEmitidoPor().getNome() : "Sistema";
        String avaliador = pagamento.getAprovadoPor() != null ? pagamento.getAprovadoPor().getNome() : null;
        String forma = pagamento.getTipoPagamento() != null ? pagamento.getTipoPagamento().getDescricao() : "não informada";

        historico.add(new EventoPagamentoDto(
                "Cobrança emitida",
                "Cobrança de R$" + pagamento.getValorPagamento() + " gerada na forma " + forma + ".",
                emitente,
                pagamento.getDataCriacao(),
                StatusPagamentoEnum.PENDENTE
        ));

        if (pagamento.getDataPagamento() != null) {
            historico.add(new EventoPagamentoDto(
                    "Pagamento confirmado",
                    avaliador != null
                            ? "Baixa aprovada pelo atendente."
                            : "Confirmado automaticamente pelo webhook do Mercado Pago.",
                    avaliador != null ? avaliador : "Mercado Pago",
                    pagamento.getDataPagamento(),
                    StatusPagamentoEnum.APROVADO
            ));
        }

        if (pagamento.getMotivoIndeferimento() != null && !pagamento.getMotivoIndeferimento().isBlank()) {
            historico.add(new EventoPagamentoDto(
                    "Pagamento indeferido",
                    pagamento.getMotivoIndeferimento(),
                    avaliador != null ? avaliador : "Sistema",
                    pagamento.getDataAtualizacao(),
                    StatusPagamentoEnum.REPROVADO
            ));
        }

        StatusPagamentoEnum status = pagamento.getStatusPagamento();
        if (status == StatusPagamentoEnum.CANCELADO
                || status == StatusPagamentoEnum.RECUSADO
                || status == StatusPagamentoEnum.DEVOLVIDO) {
            historico.add(new EventoPagamentoDto(
                    "Pagamento " + status.getDescricao().toLowerCase(),
                    "Status informado pelo Mercado Pago.",
                    "Mercado Pago",
                    pagamento.getDataAtualizacao(),
                    status
            ));
        }

        historico.sort(Comparator.comparing(EventoPagamentoDto::getData, Comparator.nullsLast(Comparator.naturalOrder())));
        return historico;
    }

    /**
     * Avisa o cliente da consulta que o pagamento foi indeferido — o mesmo aviso
     * enviado quando um pagamento é avaliado pela tela de consultas.
     */
    private void notificarClienteIndeferimento(PagamentoModel pagamento) {
        this.consultaRepository.findByPagamento_Id(pagamento.getId()).ifPresent(consulta -> {
            if (consulta.getSolicitante() == null) return;
            NovaNotificacaoForm notificacao = new NovaNotificacaoForm(
                    consulta.getSolicitante().getId(),
                    "Pagamento de Consulta",
                    "Pagamento REPROVADO por atendente!",
                    TiposNotificacoesEnum.CONSULTA
            );
            try {
                this.notificacoesController.enviarNotificacao(notificacao);
            } catch (Exception e) {
                log.error("Problema ao enviar notificação de indeferimento de pagamento ao cliente!", e);
            }
        });
    }
}
