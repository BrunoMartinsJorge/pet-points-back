package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.forms;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PrescricaoForm {

    private Long idConsulta;
    private String observacoes;
}
