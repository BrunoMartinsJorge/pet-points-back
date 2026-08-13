package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.forms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemCobrancaForm {

    private Long idProduto;
    private int quantidade;
}
