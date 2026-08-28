package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClinicaPrescricaoDto {

    private String endereco;
    private String telefone;
}
