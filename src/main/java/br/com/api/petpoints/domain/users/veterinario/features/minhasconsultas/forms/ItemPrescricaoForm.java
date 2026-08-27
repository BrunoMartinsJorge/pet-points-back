package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.forms;

import lombok.Getter;

@Getter
public class ItemPrescricaoForm {
    private Long id;
    private String dose;
    private String via;
    private String intervalo;
    private String duracao;
}
