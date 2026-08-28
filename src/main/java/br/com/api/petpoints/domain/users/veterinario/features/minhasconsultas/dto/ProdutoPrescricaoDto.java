package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoPrescricaoDto {

    private String medicamento;
    private String apresentacao;
    private String dose;
    private String via;
    private String intervalo;
    private String duracao;
}
