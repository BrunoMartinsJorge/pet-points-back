package br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.forms;

import jakarta.validation.constraints.NotEmpty;
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
}
