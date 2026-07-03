package br.com.api.petpoints.domain.users.atendente.features.consultas.forms;

import lombok.Getter;

@Getter
public class IndeferirConsultaForm {
    private Long idConsulta;
    private String motivo;
}
