package br.com.api.petpoints.domain.users.cliente.features.meuspagamentos.dto;

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
public class PagamentosDto {

    private Long id;
    private Long idConsulta;
    private double valor;
    private String dataLimitePagamento;
    private StatusPagamentoEnum statusPagamento;
    private TipoPagamentoEnum tipoPagamento;
    private boolean atrasado;

    public PagamentosDto(ConsultaModel consulta) {
        this.id = consulta.getPagamento().getId();
        this.idConsulta = consulta.getId();
        this.valor = consulta.getPagamento().getValorPagamento();
        this.dataLimitePagamento = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getPagamento().getDataPagamento());
        this.statusPagamento = consulta.getPagamento().getStatusPagamento();
        this.tipoPagamento = consulta.getPagamento().getTipoPagamento();
        this.atrasado = consulta.getPagamento().getDataPagamento().isBefore(LocalDateTime.now());
    }

    public static List<PagamentosDto> convert(List<ConsultaModel> consultas) {
        return consultas.stream().map(PagamentosDto::new).toList();
    }
}
