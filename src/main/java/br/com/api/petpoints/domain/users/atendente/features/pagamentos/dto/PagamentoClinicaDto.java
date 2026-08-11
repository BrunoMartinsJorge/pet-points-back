package br.com.api.petpoints.domain.users.atendente.features.pagamentos.dto;

import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import br.com.api.petpoints.shared.models.PagamentoModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PagamentoClinicaDto {

    private Long id;
    private String cliente;
    private String aprovadoPor;
    private String motivoIndeferimento;
    private BigDecimal valor;
    private LocalDateTime dataPagamento;
    private TipoPagamentoEnum forma;
    private StatusPagamentoEnum status;

    public PagamentoClinicaDto(PagamentoModel pagamento) {
        this.id = pagamento.getId();
        this.cliente = pagamento.getEmitidoPor().getNome();
        this.aprovadoPor = pagamento.getAprovadoPor() != null ? pagamento.getAprovadoPor().getNome() : "";
        this.valor = pagamento.getValorPagamento();
        this.dataPagamento = pagamento.getDataPagamento();
        this.forma = pagamento.getTipoPagamento();
        this.status = pagamento.getStatusPagamento();
    }

    public static List<PagamentoClinicaDto> convert(List<PagamentoModel> pagamentos) {
        return pagamentos.stream().map(PagamentoClinicaDto::new).toList();
    }
}
