package br.com.api.petpoints.modules.users.atendente.features.consultas.dto;

import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import br.com.api.petpoints.shared.models.PagamentoModel;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InformacoesPagamentoDto {

    private Long id;
    private double valor;
    private String dataLimite;
    private String enviadoEm;
    private UUID comprovante;
    private TipoPagamentoEnum formaPagamento;
    private String motivoIndeferimento;
    private StatusPagamentoEnum status;

    public InformacoesPagamentoDto(PagamentoModel pagamento) {
        this.id = pagamento.getId();
        this.valor = pagamento.getValorPagamento();
        this.dataLimite = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(pagamento.getDataLimitePagamento());
        this.comprovante = pagamento.getComprovante() != null ? pagamento.getComprovante().getArquivo() : null;
        this.motivoIndeferimento = pagamento.getMotivoIndeferimento();
        this.formaPagamento = pagamento.getTipoPagamento();
        this.status = pagamento.getStatusPagamento();
        this.enviadoEm = pagamento.getComprovante() != null ? LocalDateTimeUtils.converterLocalDateTimeParaPtBr(pagamento.getComprovante().getEnviadoEm()) : null;
    }
}
