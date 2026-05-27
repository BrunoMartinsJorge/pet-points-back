package br.com.api.petpoints.modules.users.cliente.features.dashboard.dto;

import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import br.com.api.petpoints.shared.models.ConsultaModel;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PagamentosPendentesDto {

    private Long id;
    private String dataLimitePagamento;
    private TipoPagamentoEnum tipoPagamento;
    private StatusPagamentoEnum statusPagamento;
    private double valor;
    private boolean atrasado;

    public PagamentosPendentesDto(ConsultaModel consulta) {
        this.id = consulta.getPagamento().getId();
        this.dataLimitePagamento = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getPagamento().getDataLimitePagamento());
        this.tipoPagamento = consulta.getPagamento().getTipoPagamento();
        this.statusPagamento = consulta.getPagamento().getStatusPagamento();
        this.valor = consulta.getPagamento().getValorPagamento();
        this.atrasado = LocalDateTime.now().isAfter(consulta.getPagamento().getDataLimitePagamento());
    }

    public static List<PagamentosPendentesDto> convert(List<ConsultaModel> consultas) {
        return consultas.stream().map(PagamentosPendentesDto::new).toList();
    }
}
