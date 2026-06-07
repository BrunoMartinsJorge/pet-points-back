package br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.dto;

import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import br.com.api.petpoints.shared.models.PagamentoModel;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PagamentoDto {

    private Long id;
    private TipoPagamentoEnum forma;
    private double valor;
    private String dataLimite;
    private StatusPagamentoEnum status;
    private String motivoIndeferimento;
    private String dataPagamento;
    private boolean atrasado;
    private byte[] comprovante;
    private String tipoArquivo;

    public PagamentoDto(PagamentoModel pagamento, byte[] comprovante, String tipoArquivo) {
        this.id = pagamento.getId();
        this.forma = pagamento.getTipoPagamento();
        this.valor = pagamento.getValorPagamento();
        this.dataLimite = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(pagamento.getDataLimitePagamento());
        this.status = pagamento.getStatusPagamento();
        this.motivoIndeferimento = pagamento.getMotivoIndeferimento();
        this.dataPagamento = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(pagamento.getDataPagamento());
        this.atrasado = LocalDateTime.now().isAfter(pagamento.getDataLimitePagamento()) && pagamento.getStatusPagamento().equals(StatusPagamentoEnum.PENDENTE);
        this.comprovante = comprovante;
        this.tipoArquivo = tipoArquivo;
    }
}
