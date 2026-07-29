package br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto;

import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import br.com.api.petpoints.shared.features.payment.dto.PagamentoDto;
import br.com.api.petpoints.shared.models.PagamentoModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PagamentoConsultaDto {

    private Long id;
    private StatusPagamentoEnum status;
    private BigDecimal valor;
    private TipoPagamentoEnum formaPagamento;
    private PagamentoPixDto pixPagamento;

    public record PagamentoPixDto(
            String ordemId,
            Long pagamentoId,
            String statusPagamentoOnline,
            String urlPagamento,
            String qrCodeBase
    ) {
    }

    public PagamentoConsultaDto(PagamentoModel pagamento) {
        this.id = pagamento.getId();
        this.status = pagamento.getStatusPagamento();
        this.valor = pagamento.getValorPagamento();
        this.formaPagamento = pagamento.getTipoPagamento();
    }

    public PagamentoConsultaDto(PagamentoModel pagamento,
                                PagamentoDto.PagamentoPixResponse apiResponse) {

        this.id = pagamento.getId();
        this.status = pagamento.getStatusPagamento();
        this.valor = pagamento.getValorPagamento();
        this.formaPagamento = pagamento.getTipoPagamento();

        this.pixPagamento = new PagamentoPixDto(
                apiResponse.orderId(),
                apiResponse.pagamentoId(),
                apiResponse.status(),
                apiResponse.ticketUrl(),
                apiResponse.qrCodeBase64()
        );
    }
}
