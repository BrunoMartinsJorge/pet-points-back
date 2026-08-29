package br.com.api.petpoints.shared.form;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class AvaliacaoForm {

    @NotNull(message = "O campo 'pontuação' não pode ser nulo!")
    private int pontuacao;

    @Length(min = 10, max = 150)
    private String observacoes;
}
