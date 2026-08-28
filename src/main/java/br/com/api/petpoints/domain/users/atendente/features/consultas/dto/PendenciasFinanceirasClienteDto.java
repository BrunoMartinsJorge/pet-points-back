package br.com.api.petpoints.domain.users.atendente.features.consultas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Resumo financeiro do cliente exibido ao atendente antes de aprovar uma nova
 * solicitação de consulta.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PendenciasFinanceirasClienteDto {

    private Long idCliente;
    private String cliente;
    private String email;
    private int quantidadePendentes;
    private int quantidadeAtrasadas;
    private BigDecimal valorTotalPendente;
    private BigDecimal valorTotalAtrasado;
    private List<PendenciaPagamentoClienteDto> pendencias;
}
