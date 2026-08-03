package br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.forms;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReagendamentoConsultaForm {

    private Long idConsulta;
    private LocalDateTime dataConsulta;
}
