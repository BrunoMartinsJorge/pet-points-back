package br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.forms;

import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SolicitacaoConsultaForm {

    @NotNull
    private Long idPet;
    @NotNull
    private Long idVeterinario;
    @NotNull
    private Long idTipoConsulta;
    private LocalDateTime dataConsulta;
    private String observacoes;
    private TipoPagamentoEnum formaPagamento;
}
