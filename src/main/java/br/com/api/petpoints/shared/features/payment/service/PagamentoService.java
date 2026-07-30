package br.com.api.petpoints.shared.features.payment.service;

import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import br.com.api.petpoints.shared.features.payment.dto.MercadoPagoDto;
import br.com.api.petpoints.shared.features.payment.dto.PagamentoDto;
import br.com.api.petpoints.shared.models.PagamentoModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final MercadoPagoService mercadoPagoService;
    private final PagamentoRepository pagamentoRepository;

    private PagamentoModel getPagamentoPorId(Long idPagamento) {
        return pagamentoRepository.findById(idPagamento)
                .orElseThrow(() -> {
                    log.warn("Pagamento {} não encontrado.", idPagamento);
                    return new EntityNotFoundException("Pagamento não encontrado: " + idPagamento);
                });
    }

    /**
     * Cria uma cobrança PIX utilizando o Mercado Pago e persiste as informações
     * do pagamento em banco de dados.
     * <p>
     * O fluxo consiste em:
     * <ol>
     *     <li>Criar a ordem de pagamento no Mercado Pago;</li>
     *     <li>Persistir o pagamento localmente;</li>
     *     <li>Retornar os dados necessários para pagamento via PIX.</li>
     * </ol>
     *
     * @param form Dados necessários para criação da cobrança.
     *
     * @return Informações da cobrança PIX.
     */
    @Transactional
    public PagamentoDto.PagamentoPixResponse criarPagamentoPix(PagamentoDto.CriarPagamentoPixForm form, UsuarioModel usuario) {

        log.info(
                "Iniciando geração de pagamento PIX. Consulta: {}, CPF: {}, Valor: {}",
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

        log.info(
                "Ordem criada no Mercado Pago. OrderId: {}, Status: {}",
                order.id(),
                order.status());

        PagamentoModel pagamento = new PagamentoModel();
        pagamento.setValorPagamento(form.valor());
        pagamento.setIdPagamentoExterno(order.id());
        pagamento.setTipoPagamento(TipoPagamentoEnum.PIX);
        pagamento.setStatusPagamento(mapearStatus(order.status()));
        pagamento.setDataLimitePagamento(LocalDateTime.now().plusWeeks(1));
        pagamento.setEmitidoPor(usuario);

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
     * Consulta o status atualizado de um pagamento.
     * <p>
     * A consulta é realizada tanto na base local quanto na API do Mercado Pago.
     * Caso o status tenha sido alterado, o registro local é atualizado.
     *
     * @param pagamentoId Identificador interno do pagamento.
     *
     * @return Status atualizado do pagamento.
     */
    @Transactional
    public PagamentoDto.StatusPagamentoResponse consultarStatus(Long pagamentoId) {

        log.info("Consultando status do pagamento {}.", pagamentoId);

        PagamentoModel pagamento = this.getPagamentoPorId(pagamentoId);

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
        log.info("Consultando do pagamento {}.", idPagamento);

        PagamentoModel pagamento = this.getPagamentoPorId(idPagamento);

        return mercadoPagoService.buscarOrder(pagamento.getIdPagamentoExterno());
    }

    /**
     * Processa notificações enviadas pelo webhook do Mercado Pago.
     * <p>
     * Ao receber uma notificação, o serviço localiza o pagamento correspondente,
     * consulta o status atualizado na API do Mercado Pago e sincroniza as
     * informações armazenadas localmente.
     * </p>
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
     * Mercado Pago.
     *
     * @param pagamento Pagamento persistido.
     * @param order     Ordem retornada pela API do Mercado Pago.
     */
    private void atualizarPagamento(PagamentoModel pagamento,
                                    MercadoPagoDto.OrderResponse order) {

        StatusPagamentoEnum statusAnterior = pagamento.getStatusPagamento();
        StatusPagamentoEnum novoStatus = mapearStatus(order.status());

        pagamento.setStatusPagamento(novoStatus);

        if (novoStatus == StatusPagamentoEnum.PAGO &&
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
        } else {
            log.debug(
                    "Pagamento {} permanece com status {}.",
                    pagamento.getId(),
                    novoStatus);
        }
    }

    /**
     * Converte o status retornado pelo Mercado Pago para o
     * {@link StatusPagamentoEnum} utilizado pela aplicação.
     *
     * @param statusMp Status retornado pela API do Mercado Pago.
     *
     * @return Status equivalente utilizado internamente.
     */
    private StatusPagamentoEnum mapearStatus(String statusMp) {

        if (statusMp == null) {
            return StatusPagamentoEnum.PENDENTE;
        }

        return switch (statusMp) {
            case "processed" -> StatusPagamentoEnum.PAGO;
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
     *
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
     *
     * @return Valor com duas casas decimais.
     */
    private String formatarValor(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
