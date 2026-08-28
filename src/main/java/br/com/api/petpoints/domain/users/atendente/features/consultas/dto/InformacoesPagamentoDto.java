package br.com.api.petpoints.domain.users.atendente.features.consultas.dto;

import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import br.com.api.petpoints.shared.models.PagamentoModel;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Resumo somente leitura da cobrança de uma consulta. A avaliação da cobrança
 * (baixa e indeferimento) é feita na tela de Pagamentos da Clínica, então aqui
 * apenas informamos a situação atual ao atendente.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InformacoesPagamentoDto {

    private Long id;
    private BigDecimal valor;
    private String dataLimite;
    private String emitidoEm;
    private String pagoEm;
    private TipoPagamentoEnum formaPagamento;
    private String motivoIndeferimento;
    private String avaliadoPor;
    private StatusPagamentoEnum status;

    public InformacoesPagamentoDto(PagamentoModel pagamento) {
        this.id = pagamento.getId();
        this.valor = pagamento.getValorPagamento();
        this.dataLimite = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(pagamento.getDataLimitePagamento());
        this.emitidoEm = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(pagamento.getDataCriacao());
        this.pagoEm = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(pagamento.getDataPagamento());
        this.motivoIndeferimento = pagamento.getMotivoIndeferimento();
        this.formaPagamento = pagamento.getTipoPagamento();
        this.avaliadoPor = pagamento.getAprovadoPor() != null ? pagamento.getAprovadoPor().getNome() : null;
        this.status = pagamento.getStatusPagamento();
    }
}
