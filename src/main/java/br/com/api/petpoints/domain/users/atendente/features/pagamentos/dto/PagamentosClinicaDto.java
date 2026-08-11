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
public class PagamentosClinicaDto {

    private Long id;
    private BigDecimal valor;
    private TipoPagamentoEnum forma;
    private LocalDateTime dataPagamento;
    private String cliente;
    private String atendente;
    private StatusPagamentoEnum status;
    private String motivoIndeferimento;

    public PagamentosClinicaDto(PagamentoModel pagamento) {
        this.id = pagamento.getId();
        this.valor = pagamento.getValorPagamento();
        this.forma = pagamento.getTipoPagamento();
        this.dataPagamento = pagamento.getDataPagamento() != null ? pagamento.getDataPagamento() : null;
        this.cliente = pagamento.getEmitidoPor().getNome();
        this.atendente = pagamento.getAprovadoPor() != null ? pagamento.getAprovadoPor().getNome() : "Não Aprovação.";
        this.status = pagamento.getStatusPagamento();
        this.motivoIndeferimento = pagamento.getMotivoIndeferimento();
    }

    public static List<PagamentosClinicaDto> convert(List<PagamentoModel> pagamentos) {
        return pagamentos.stream().map(PagamentosClinicaDto::new).toList();
    }
}
