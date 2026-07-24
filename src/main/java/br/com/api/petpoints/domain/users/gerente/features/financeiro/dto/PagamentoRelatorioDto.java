package br.com.api.petpoints.domain.users.gerente.features.financeiro.dto;

import br.com.api.petpoints.shared.models.PagamentoModel;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PagamentoRelatorioDto {

    private String dataPagamento;
    private String cliente;
    private String tipoPagamento;
    private String statusPagamento;
    private double valor;
    private String aprovadoPor;

    public PagamentoRelatorioDto(PagamentoModel pagamento) {
        this.dataPagamento = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(pagamento.getDataPagamento());
        this.cliente = pagamento.getEmitidoPor() != null ? pagamento.getEmitidoPor().getNome() : "-";
        this.tipoPagamento = pagamento.getTipoPagamento() != null ? pagamento.getTipoPagamento().getDescricao() : "-";
        this.statusPagamento = pagamento.getStatusPagamento().getDescricao();
        this.valor = pagamento.getValorPagamento();
        this.aprovadoPor = pagamento.getAprovadoPor() != null ? pagamento.getAprovadoPor().getNome() : "-";
    }

    public static List<PagamentoRelatorioDto> convert(List<PagamentoModel> pagamentos) {
        return pagamentos.stream().map(PagamentoRelatorioDto::new).toList();
    }
}
