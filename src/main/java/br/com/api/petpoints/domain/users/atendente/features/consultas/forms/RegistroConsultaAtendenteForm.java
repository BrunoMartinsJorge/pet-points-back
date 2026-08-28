package br.com.api.petpoints.domain.users.atendente.features.consultas.forms;

import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Dados enviados pelo atendente para registrar uma consulta diretamente,
 * sem passar pelo fluxo de solicitação/deferimento. A consulta já nasce APROVADA.
 */
@Getter
public class RegistroConsultaAtendenteForm {

    @NotNull
    private Long idCliente;
    @NotNull
    private Long idPet;
    @NotNull
    private Long idVeterinario;
    @NotNull
    private Long idTipoConsulta;
    @NotNull
    private LocalDateTime dataConsulta;
    private String observacoes;
    @NotNull
    private TipoPagamentoEnum formaPagamento;
}
