package br.com.api.petpoints.domain.users.atendente.features.pagamentos.dto;

import br.com.api.petpoints.shared.features.payment.dto.MercadoPagoDto;
import br.com.api.petpoints.shared.models.PagamentoModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransacaoPagamentoDto {

    private static final String GATEWAY = "Mercado Pago";

    private String identificador;
    private String gateway;
    private String metodo;
    private String statusGateway;
    private String detalheStatus;
    private String valorTotal;
    private String valorPago;
    private LocalDateTime dataProcessamento;
    private LocalDateTime ultimaSincronizacao;

    /**
     * Monta a transação apenas com o que já está persistido localmente, sem
     * chamar o Mercado Pago. Usado ao abrir os detalhes do pagamento, para que
     * a tela não dependa da disponibilidade do gateway.
     */
    public TransacaoPagamentoDto(PagamentoModel pagamento) {
        this.identificador = pagamento.getIdPagamentoExterno();
        this.gateway = GATEWAY;
        this.metodo = pagamento.getTipoPagamento() != null ? pagamento.getTipoPagamento().getDescricao() : null;
        this.valorTotal = pagamento.getValorPagamento() != null ? pagamento.getValorPagamento().toPlainString() : null;
        this.dataProcessamento = pagamento.getDataPagamento();
        this.ultimaSincronizacao = pagamento.getDataAtualizacao();
    }

    /**
     * Complementa a transação com os dados vindos da ordem consultada no
     * Mercado Pago.
     */
    public void preencherComOrdem(MercadoPagoDto.OrderResponse order) {
        if (order == null) return;
        this.identificador = order.id() != null ? order.id() : this.identificador;
        this.statusGateway = order.status();
        this.detalheStatus = order.statusDetail();
        this.valorTotal = order.totalAmount() != null ? order.totalAmount() : this.valorTotal;
        this.valorPago = order.totalPaidAmount();
        this.ultimaSincronizacao = LocalDateTime.now();
    }
}
