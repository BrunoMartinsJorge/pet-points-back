package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.forms;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PrescricaoForm {

    private Long idConsulta;
    private String observacoes;
    private String diagnostico;
    private List<ItemPrescricaoForm> itens;
    private LocalDateTime retorno;
}
