package br.com.api.petpoints.shared.features.payment.service;

import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import br.com.api.petpoints.shared.enums.TiposNotificacoesEnum;
import br.com.api.petpoints.shared.features.notificacoes.controller.NotificacoesController;
import br.com.api.petpoints.shared.features.notificacoes.form.NovaNotificacaoForm;
import br.com.api.petpoints.shared.features.payment.dto.MercadoPagoDto;
import br.com.api.petpoints.shared.features.payment.dto.PagamentoDto;
import br.com.api.petpoints.shared.models.ConsultaModel;
import br.com.api.petpoints.shared.models.PagamentoModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import br.com.api.petpoints.shared.repository.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class PagamentoService {

    private final MercadoPagoService mercadoPagoService;
    private final PagamentoRepository pagamentoRepository;
    private final ConsultaRepository consultaRepository;
    private final NotificacoesController notificacoesController;

    public PagamentoService(MercadoPagoService mercadoPagoService,
                            PagamentoRepository pagamentoRepository,
                            ConsultaRepository consultaRepository,
                            NotificacoesController notificacoesController) {
        this.mercadoPagoService = mercadoPagoService;
        this.pagamentoRepository = pagamentoRepository;
        this.consultaRepository = consultaRepository;
        this.notificacoesController = notificacoesController;
    }

    /**
     * Gera (ou regenera) uma cobrança PIX no Mercado Pago para um pagamento.
     * <p>
     * Aceita um {@link PagamentoModel} novo (ainda não persistido) ou já
     * existente (quando o cliente troca a forma de pagamento de volta para
     * PIX) — os campos são sobrescritos com os dados da nova ordem.
     *
     * @param pagamento  Pagamento a ser atualizado com os dados do PIX.
     * @param form       Dados necessários para criação da cobrança.
     * @param emitidoPor Usuário responsável por emitir a cobrança (normalmente o veterinário/atendente).
     * @return Informações da cobrança PIX (QR Code, etc.).
     */
    @Transactional
    public PagamentoDto.PagamentoPixResponse gerarCobrancaPix(PagamentoModel pagamento,
                                                              PagamentoDto.CriarPagamentoPixForm form,
                                                              UsuarioModel emitidoPor) {

        log.info(
                "Iniciando geração de pagamento PIX. Referência: {}, CPF: {}, Valor: {}",
                form.externalReference(),
                form.cpf(),
                form.valor());

        String valorFormatado = formatarValor(form.valor());

        var request = new MercadoPagoDto.OrderRequest(
                "online",
                form.externalReference(),
                "automatic",
                valorFormatado,
                form.descricao(),
                new MercadoPagoDto.OrderRequest.Payer(
                        form.emailPagador(),
                        form.nomePagador(),
                        new MercadoPagoDto.OrderRequest.Identification("CPF", form.cpf())
                ),
                new MercadoPagoDto.OrderRequest.Transactions(List.of(
                        new MercadoPagoDto.OrderRequest.Payment(
                                valorFormatado,
                                new MercadoPagoDto.OrderRequest.PaymentMethod("pix", "bank_transfer")
                        )
                ))
        );

        log.debug("Enviando requisição para criação da ordem no Mercado Pago.");

        MercadoPagoDto.OrderResponse order = mercadoPagoService.criarOrder(request);

        log.info("Ordem criada no Mercado Pago. OrderId: {}, Status: {}", order.id(), order.status());

        pagamento.setValorPagamento(form.valor());
        pagamento.setIdPagamentoExterno(order.id());
        pagamento.setTipoPagamento(TipoPagamentoEnum.PIX);
        pagamento.setStatusPagamento(mapearStatus(order.status()));
        pagamento.setDataLimitePagamento(LocalDateTime.now().plusWeeks(1));
        pagamento.setDataPagamento(null);
        pagamento.setMotivoIndeferimento(null);
        pagamento.setAprovadoPor(null);
        if (emitidoPor != null) {
            pagamento.setEmitidoPor(emitidoPor);
        }

        return getPagamentoPixResponse(order, pagamento);
    }

    public PagamentoDto.@NonNull PagamentoPixResponse getPagamentoPixResponse(MercadoPagoDto.OrderResponse order, PagamentoModel pagamento) {
        pagamentoRepository.save(pagamento);

        log.info(
                "Pagamento salvo com sucesso. Id interno: {}, OrderId: {}",
                pagamento.getId(),
                pagamento.getIdPagamentoExterno());

        MercadoPagoDto.OrderResponse.PaymentMethod pm = extrairPaymentMethod(order);

        return new PagamentoDto.PagamentoPixResponse(
                pagamento.getId(),
                order.id(),
                pagamento.getStatusPagamento().name(),
                order.totalAmount(),
                pm != null ? pm.qrCode() : null,
                pm != null ? pm.qrCodeBase64() : null,
                pm != null ? pm.ticketUrl() : null
        );
    }

    /**
     * Gera (ou regenera) uma cobrança presencial (dinheiro ou cartão sem integração
     * online). Não há chamada ao Mercado Pago: o pagamento fica com status
     * {@code PENDENTE} até que o atendente confirme o recebimento no balcão,
     * através de {@code avaliarPagamento}.
     *
     * @param pagamento  Pagamento a ser atualizado.
     * @param tipo       {@link TipoPagamentoEnum#DINHEIRO} ou {@link TipoPagamentoEnum#CARTAO}.
     * @param valor      Valor da cobrança.
     * @param emitidoPor Usuário responsável por emitir a cobrança (normalmente o veterinário/atendente).
     * @return Pagamento persistido.
     */
    @Transactional
    public PagamentoModel criarPagamentoPresencial(PagamentoModel pagamento,
                                                   TipoPagamentoEnum tipo,
                                                   BigDecimal valor,
                                                   UsuarioModel emitidoPor) {

        if (tipo == TipoPagamentoEnum.PIX) {
            throw new IllegalArgumentException("Pagamento presencial não pode ser do tipo PIX.");
        }

        log.info("Gerando cobrança presencial ({}) no valor de {}.", tipo, valor);

        pagamento.setValorPagamento(valor);
        pagamento.setTipoPagamento(tipo);
        pagamento.setStatusPagamento(StatusPagamentoEnum.PENDENTE);
        pagamento.setIdPagamentoExterno(null);
        pagamento.setDataPagamento(null);
        pagamento.setMotivoIndeferimento(null);
        pagamento.setAprovadoPor(null);
        pagamento.setEmitidoPor(emitidoPor);

        return pagamentoRepository.save(pagamento);
    }

    /**
     * Cancela, no Mercado Pago, uma ordem PIX ainda pendente. Usado quando o
     * cliente troca a forma de pagamento antes de o PIX ser pago. Falhas de
     * cancelamento (ex.: ordem já paga/expirada) são apenas registradas em log
     * — quem chama este método já deve ter validado que o pagamento ainda não
     * foi confirmado antes de permitir a troca.
     *
     * @param pagamento Pagamento cuja ordem externa deve ser cancelada.
     */
    @Transactional
    public void cancelarPagamentoPix(PagamentoModel pagamento) {
        if (pagamento.getIdPagamentoExterno() == null) {
            return;
        }
        try {
            mercadoPagoService.cancelarOrder(pagamento.getIdPagamentoExterno());
        } catch (MercadoPagoService.MercadoPagoException e) {
            log.warn(
                    "Não foi possível cancelar a ordem {} no Mercado Pago (pode já estar paga ou expirada): {}",
                    pagamento.getIdPagamentoExterno(),
                    e.getMessage());
        }
    }

    /**
     * Consulta o status atualizado de um pagamento.
     * <p>
     * A consulta é realizada tanto na base local quanto na API do Mercado Pago.
     * Caso o status tenha sido alterado, o registro local é atualizado.
     *
     * @param pagamentoId Identificador interno do pagamento.
     * @return Status atualizado do pagamento.
     */
    @Transactional
    public PagamentoDto.StatusPagamentoResponse consultarStatus(Long pagamentoId) {

        log.info("Consultando status do pagamento {}.", pagamentoId);

        PagamentoModel pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> {
                    log.warn("Pagamento {} não encontrado.", pagamentoId);
                    return new EntityNotFoundException("Pagamento não encontrado: " + pagamentoId);
                });

        MercadoPagoDto.OrderResponse order =
                mercadoPagoService.buscarOrder(pagamento.getIdPagamentoExterno());

        atualizarPagamento(pagamento, order);

        log.info(
                "Status atualizado. Pagamento: {}, Status interno: {}, Status Mercado Pago: {}",
                pagamento.getId(),
                pagamento.getStatusPagamento(),
                order.status());

        return new PagamentoDto.StatusPagamentoResponse(
                pagamento.getId(),
                order.id(),
                pagamento.getStatusPagamento().name(),
                order.status(),
                order.statusDetail()
        );
    }

    public MercadoPagoDto.OrderResponse buscarPagamentoPix(Long idPagamento) {
        log.info("Consultando status do pagamento {}.", idPagamento);

        PagamentoModel pagamento = pagamentoRepository.findById(idPagamento)
                .orElseThrow(() -> {
                    log.warn("Pagamento {} não encontrado.", idPagamento);
                    return new EntityNotFoundException("Pagamento não encontrado: " + idPagamento);
                });

        return mercadoPagoService.buscarOrder(pagamento.getIdPagamentoExterno());
    }

    /**
     * Processa notificações enviadas pelo webhook do Mercado Pago.
     * <p>
     * Ao receber uma notificação, o serviço localiza o pagamento correspondente,
     * consulta o status atualizado na API do Mercado Pago e sincroniza as
     * informações armazenadas localmente. É assim que o sistema "fica sabendo"
     * que um PIX foi pago, sem precisar de nenhuma ação manual do atendente.
     *
     * @param idRecebido Identificador enviado pelo Mercado Pago.
     */
    @Transactional
    public void processarWebhook(String idRecebido) {

        log.info("Webhook recebido. Id externo: {}", idRecebido);

        pagamentoRepository.findByIdPagamentoExterno(idRecebido)
                .ifPresentOrElse(pagamento -> {

                    log.info(
                            "Pagamento localizado para processamento do webhook. Id interno: {}",
                            pagamento.getId());

                    MercadoPagoDto.OrderResponse order =
                            mercadoPagoService.buscarOrder(idRecebido);

                    atualizarPagamento(pagamento, order);

                    log.info(
                            "Webhook processado com sucesso. Pagamento: {}, Novo status: {}",
                            pagamento.getId(),
                            pagamento.getStatusPagamento());

                }, () -> log.warn(
                        "Webhook recebido para OrderId {} sem pagamento correspondente.",
                        idRecebido));
    }

    /**
     * Atualiza o status do pagamento local de acordo com o status retornado pelo
     * Mercado Pago e avisa o cliente pelo sistema de notificações quando o
     * status muda para um estado final (aprovado, recusado, cancelado, devolvido).
     *
     * @param pagamento Pagamento persistido.
     * @param order Ordem retornada pela API do Mercado Pago.
     */
    private void atualizarPagamento(PagamentoModel pagamento,
                                    MercadoPagoDto.OrderResponse order) {

        StatusPagamentoEnum statusAnterior = pagamento.getStatusPagamento();
        StatusPagamentoEnum novoStatus = mapearStatus(order.status());

        pagamento.setStatusPagamento(novoStatus);

        if (novoStatus == StatusPagamentoEnum.APROVADO &&
                pagamento.getDataPagamento() == null) {

            pagamento.setDataPagamento(LocalDateTime.now());

            log.info(
                    "Pagamento {} confirmado em {}.",
                    pagamento.getId(),
                    pagamento.getDataPagamento());
        }

        pagamentoRepository.save(pagamento);

        if (statusAnterior != novoStatus) {
            log.info(
                    "Status alterado. Pagamento: {}, {} -> {}",
                    pagamento.getId(),
                    statusAnterior,
                    novoStatus);
            notificarClienteMudancaStatus(pagamento, novoStatus);
        } else {
            log.debug(
                    "Pagamento {} permanece com status {}.",
                    pagamento.getId(),
                    novoStatus);
        }
    }

    /**
     * Avisa o cliente (via notificação in-app) quando o Mercado Pago confirma,
     * recusa, cancela ou devolve um pagamento — o mesmo tipo de aviso que o
     * cliente já recebe quando um atendente avalia um pagamento presencial.
     */
    private void notificarClienteMudancaStatus(PagamentoModel pagamento, StatusPagamentoEnum novoStatus) {
        if (novoStatus != StatusPagamentoEnum.APROVADO
                && novoStatus != StatusPagamentoEnum.RECUSADO
                && novoStatus != StatusPagamentoEnum.CANCELADO
                && novoStatus != StatusPagamentoEnum.DEVOLVIDO) {
            return;
        }

        consultaRepository.findByPagamento_Id(pagamento.getId()).ifPresent(consulta -> {
            if (consulta.getSolicitante() == null) return;

            String mensagem = switch (novoStatus) {
                case APROVADO -> "Pagamento via PIX confirmado com sucesso!";
                case RECUSADO -> "O pagamento via PIX foi recusado.";
                case CANCELADO -> "O pagamento via PIX foi cancelado.";
                case DEVOLVIDO -> "O pagamento via PIX foi devolvido.";
                default -> "O status do seu pagamento foi atualizado.";
            };

            NovaNotificacaoForm notificacao = new NovaNotificacaoForm(
                    consulta.getSolicitante().getId(),
                    "Pagamento de Consulta",
                    mensagem,
                    TiposNotificacoesEnum.CONSULTA
            );
            try {
                notificacoesController.enviarNotificacao(notificacao);
            } catch (Exception e) {
                log.error("Problema ao enviar notificação de atualização de pagamento PIX ao cliente!", e);
            }
        });
    }

    /**
     * Converte o status retornado pelo Mercado Pago para o
     * {@link StatusPagamentoEnum} utilizado pela aplicação.
     *
     * @param statusMp Status retornado pela API do Mercado Pago.
     * @return Status equivalente utilizado internamente.
     */
    private StatusPagamentoEnum mapearStatus(String statusMp) {

        if (statusMp == null) {
            return StatusPagamentoEnum.PENDENTE;
        }

        return switch (statusMp) {
            case "processed" -> StatusPagamentoEnum.APROVADO;
            case "canceled", "cancelled", "expired" -> StatusPagamentoEnum.CANCELADO;
            case "refunded" -> StatusPagamentoEnum.DEVOLVIDO;
            case "failed", "rejected" -> StatusPagamentoEnum.RECUSADO;
            default -> StatusPagamentoEnum.PENDENTE;
        };
    }

    /**
     * Obtém o primeiro método de pagamento retornado pelo Mercado Pago.
     *
     * @param order Ordem consultada.
     * @return Método de pagamento ou {@code null} caso não exista.
     */
    private MercadoPagoDto.OrderResponse.PaymentMethod extrairPaymentMethod(
            MercadoPagoDto.OrderResponse order) {

        if (order.transactions() == null
                || order.transactions().payments() == null
                || order.transactions().payments().isEmpty()) {

            log.debug("Ordem {} não possui método de pagamento.", order.id());
            return null;
        }

        return order.transactions().payments().getFirst().paymentMethod();
    }

    /**
     * Formata um valor monetário para o padrão esperado pela API do Mercado Pago.
     *
     * @param valor Valor original.
     * @return Valor com duas casas decimais.
     */
    private String formatarValor(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
