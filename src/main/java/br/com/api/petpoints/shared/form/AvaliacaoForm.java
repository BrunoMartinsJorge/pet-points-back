package br.com.api.petpoints.shared.form;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AvaliacaoForm {

    @NotNull(message = "O campo 'pontuação' não pode ser nulo!")
    private int pontuacao;
    private String observacoes;
}
