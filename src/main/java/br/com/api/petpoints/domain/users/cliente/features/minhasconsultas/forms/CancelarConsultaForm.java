package br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.forms;

import lombok.Getter;

@Getter
public class CancelarConsultaForm {
    private Long idConsulta;
    private String motivoCancelamento;
}
