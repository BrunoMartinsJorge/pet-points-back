package br.com.api.petpoints.modules.users.cliente.features.meuspagamentos.dto;

import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
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
    private double valor;
    private String dataLimitePagamento;
    private boolean atrasado;
    private StatusPagamentoEnum status;

    public PagamentosPendentesDto(ConsultaModel consulta) {
        this.id = consulta.getPagamento().getId();
        this.valor = consulta.getPagamento().getValorPagamento();
        this.dataLimitePagamento = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getPagamento().getDataPagamento());
        this.atrasado = consulta.getPagamento().getDataPagamento().isAfter(LocalDateTime.now());
        this.status = consulta.getPagamento().getStatusPagamento();
    }

    public static List<PagamentosPendentesDto> convert(List<ConsultaModel> consultas) {
        return consultas.stream().map(PagamentosPendentesDto::new).toList();
    }
}
