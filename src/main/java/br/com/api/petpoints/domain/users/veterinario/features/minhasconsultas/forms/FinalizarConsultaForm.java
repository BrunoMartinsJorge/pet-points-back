package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.forms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FinalizarConsultaForm {

    private String resumo;

    /**
     * Produtos consumidos durante a consulta (vacinas, medicamentos, etc.).
     * Cada item dá baixa no estoque e entra no valor da cobrança.
     */
    private List<ItemCobrancaForm> itens = new ArrayList<>();
}
