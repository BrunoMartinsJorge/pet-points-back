package br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.forms;

import lombok.Getter;

@Getter
public class CancelarConsultaForm {
    private Long idConsulta;
    private String motivoCancelamento;
}
