package br.com.api.petpoints.domain.users.atendente.features.consultas.dto;

import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import br.com.api.petpoints.shared.models.ConsultaModel;
import br.com.api.petpoints.shared.models.PagamentoModel;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Uma cobrança em aberto do cliente, já com o contexto da consulta que a gerou.
 * Serve para o atendente decidir sobre uma nova solicitação sabendo se o cliente
 * está em dia com a clínica.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PendenciaPagamentoClienteDto {

    private Long idPagamento;
    private Long idConsulta;
    private BigDecimal valor;
    private TipoPagamentoEnum forma;
    private StatusPagamentoEnum status;
    private String dataLimite;
    private String emitidoEm;
    private String pet;
    private UUID imagemPet;
    private String tipoConsulta;
    private String dataConsulta;
    private boolean atrasado;
    private long diasEmAtraso;

    public PendenciaPagamentoClienteDto(ConsultaModel consulta, LocalDateTime referencia) {
        PagamentoModel pagamento = consulta.getPagamento();
        LocalDateTime dataLimitePagamento = pagamento.getDataLimitePagamento();

        this.idPagamento = pagamento.getId();
        this.idConsulta = consulta.getId();
        this.valor = pagamento.getValorPagamento();
        this.forma = pagamento.getTipoPagamento();
        this.status = pagamento.getStatusPagamento();
        this.dataLimite = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(dataLimitePagamento);
        this.emitidoEm = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(pagamento.getDataCriacao());
        this.pet = consulta.getPet() != null ? consulta.getPet().getNome() : null;
        this.imagemPet = consulta.getPet() != null ? consulta.getPet().getImagem() : null;
        this.tipoConsulta = consulta.getTipoConsulta() != null ? consulta.getTipoConsulta().getNome() : null;
        this.dataConsulta = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getDataConsulta());
        this.atrasado = dataLimitePagamento != null && dataLimitePagamento.isBefore(referencia);
        this.diasEmAtraso = this.atrasado
                ? ChronoUnit.DAYS.between(dataLimitePagamento.toLocalDate(), referencia.toLocalDate())
                : 0;
    }
}
