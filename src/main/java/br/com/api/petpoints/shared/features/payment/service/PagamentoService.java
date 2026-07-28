package br.com.api.petpoints.shared.features.payment.service;

import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import br.com.api.petpoints.shared.features.payment.dto.MercadoPagoDto;
import br.com.api.petpoints.shared.features.payment.dto.PagamentoDto;
import br.com.api.petpoints.shared.models.PagamentoModel;
import br.com.api.petpoints.shared.repository.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagamentoService {

    private final MercadoPagoService mercadoPagoService;
    private final PagamentoRepository pagamentoRepository;

    public PagamentoService(MercadoPagoService mercadoPagoService,
                            PagamentoRepository pagamentoRepository) {
        this.mercadoPagoService = mercadoPagoService;
        this.pagamentoRepository = pagamentoRepository;
    }

    /** Cria a order PIX no MP, persiste o pagamento e devolve o QR para o front. */
    @Transactional
    public PagamentoDto.PagamentoPixResponse criarPagamentoPix(PagamentoDto.CriarPagamentoPixForm form) {
        String valorFormatado = formatarValor(form.valor());

        // 1) Monta o corpo da requisição para o Mercado Pago
        var request = new MercadoPagoDto.OrderRequest(
                "online",
                form.externalReference(),
                "automatic",
                valorFormatado,
                form.descricao(),
                new MercadoPagoDto.OrderRequest.Payer(form.emailPagador(), form.nomePagador()),
                new MercadoPagoDto.OrderRequest.Transactions(List.of(
                        new MercadoPagoDto.OrderRequest.Payment(
                                valorFormatado,
                                new MercadoPagoDto.OrderRequest.PaymentMethod("pix", "bank_transfer")
                        )
                ))
        );

        // 2) Chama o MP
        MercadoPagoDto.OrderResponse order = mercadoPagoService.criarOrder(request);

        // 3) Persiste o PagamentoModel
        PagamentoModel pagamento = new PagamentoModel();
        pagamento.setValorPagamento(form.valor());
        pagamento.setIdPagamentoExterno(order.id());
        pagamento.setTipoPagamento(TipoPagamentoEnum.PIX);            // <-- ajuste ao seu enum
        pagamento.setStatusPagamento(mapearStatus(order.status()));
        // pagamento.setEmitidoPor(usuarioLogado);                    // setar o usuário responsável
        // pagamento.setDataLimitePagamento(LocalDateTime.now().plusMinutes(30)); // se aplicável
        pagamentoRepository.save(pagamento);

        // 4) Extrai o QR e devolve
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

    /** Reconsulta a order no MP e atualiza o status local. */
    @Transactional
    public PagamentoDto.StatusPagamentoResponse consultarStatus(Long pagamentoId) {
        PagamentoModel pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new EntityNotFoundException("Pagamento não encontrado: " + pagamentoId));

        MercadoPagoDto.OrderResponse order = mercadoPagoService.buscarOrder(pagamento.getIdPagamentoExterno());
        atualizarPagamento(pagamento, order);

        return new PagamentoDto.StatusPagamentoResponse(
                pagamento.getId(),
                order.id(),
                pagamento.getStatusPagamento().name(),
                order.status(),
                order.statusDetail()
        );
    }

    /**
     * Processa a notificação de webhook.
     * OBS: dependendo do "type" da notificação, o data.id pode ser um id de order (ORD...)
     * ou de pagamento (PAY...). Aqui procuramos pelo id externo salvo (order id).
     * Valide com notificações reais e ajuste se o MP enviar o id do pagamento.
     */
    @Transactional
    public void processarWebhook(String idRecebido) {
        pagamentoRepository.findByIdPagamentoExterno(idRecebido).ifPresent(pagamento -> {
            MercadoPagoDto.OrderResponse order = mercadoPagoService.buscarOrder(idRecebido);
            atualizarPagamento(pagamento, order);
        });
    }

    // ----------------- auxiliares -----------------

    private void atualizarPagamento(PagamentoModel pagamento, MercadoPagoDto.OrderResponse order) {
        StatusPagamentoEnum novoStatus = mapearStatus(order.status());
        pagamento.setStatusPagamento(novoStatus);

        // Marca a data do pagamento só quando ele é efetivamente aprovado.
        if (novoStatus == StatusPagamentoEnum.PAGO && pagamento.getDataPagamento() == null) {
            pagamento.setDataPagamento(LocalDateTime.now());
        }
        pagamentoRepository.save(pagamento);
    }

    /**
     * Mapeia o status do Mercado Pago para o SEU StatusPagamentoEnum.
     * >>> AJUSTE os valores da direita conforme os que existem no seu enum. <<<
     * (Você só me confirmou PENDENTE; os demais são suposições.)
     */
    private StatusPagamentoEnum mapearStatus(String statusMp) {
        if (statusMp == null) {
            return StatusPagamentoEnum.PENDENTE;
        }
        return switch (statusMp) {
            case "processed" -> StatusPagamentoEnum.PAGO;
            case "created", "action_required", "processing" -> StatusPagamentoEnum.PENDENTE;
            case "canceled", "cancelled", "expired" -> StatusPagamentoEnum.CANCELADO;
            case "refunded" -> StatusPagamentoEnum.DEVOLVIDO;
            case "failed", "rejected" -> StatusPagamentoEnum.RECUSADO;
            default -> StatusPagamentoEnum.PENDENTE;
        };
    }

    private MercadoPagoDto.OrderResponse.PaymentMethod extrairPaymentMethod(MercadoPagoDto.OrderResponse order) {
        if (order.transactions() == null
                || order.transactions().payments() == null
                || order.transactions().payments().isEmpty()) {
            return null;
        }
        return order.transactions().payments().get(0).paymentMethod();
    }

    private String formatarValor(BigDecimal valor) {
        // Garante sempre 2 casas: 200 -> "200.00"
        return valor.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
