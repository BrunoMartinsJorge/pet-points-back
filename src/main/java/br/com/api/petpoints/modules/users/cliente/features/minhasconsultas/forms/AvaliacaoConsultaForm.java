package br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.forms;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AvaliacaoConsultaForm {

    @NotNull(message = "O campo 'pontuação' não pode ser nulo!")
    private int pontuacao;
    private String observacoes;
}
