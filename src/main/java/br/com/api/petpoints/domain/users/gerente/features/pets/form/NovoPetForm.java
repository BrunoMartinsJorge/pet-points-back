package br.com.api.petpoints.domain.users.gerente.features.pets.form;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.enums.TipoAnimalEnum;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class NovoPetForm {

    private String nome;
    private TipoAnimalEnum tipo;
    private GeneroEnum genero;
    private Long idTutor;
    private String raca;
    private LocalDate dataNascimento;
    private String observacoes;
}
