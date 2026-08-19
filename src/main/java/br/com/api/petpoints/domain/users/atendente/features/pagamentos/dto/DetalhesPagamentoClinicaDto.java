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
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetalhesPagamentoClinicaDto {

    private Long id;
    private BigDecimal valor;
    private TipoPagamentoEnum forma;
    private StatusPagamentoEnum status;
    private LocalDateTime dataLimite;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataPagamento;
    private LocalDateTime dataAtualizacao;
    private String motivoIndeferimento;
    private ResponsavelPagamentoDto emitidoPor;
    private ResponsavelPagamentoDto aprovadoPor;
    private TransacaoPagamentoDto transacao;
    private List<EventoPagamentoDto> historico = new ArrayList<>();

    public DetalhesPagamentoClinicaDto(PagamentoModel pagamento) {
        this.id = pagamento.getId();
        this.valor = pagamento.getValorPagamento();
        this.forma = pagamento.getTipoPagamento();
        this.status = pagamento.getStatusPagamento();
        this.dataLimite = pagamento.getDataLimitePagamento();
        this.dataCriacao = pagamento.getDataCriacao();
        this.dataPagamento = pagamento.getDataPagamento();
        this.dataAtualizacao = pagamento.getDataAtualizacao();
        this.motivoIndeferimento = pagamento.getMotivoIndeferimento();
    }
}
