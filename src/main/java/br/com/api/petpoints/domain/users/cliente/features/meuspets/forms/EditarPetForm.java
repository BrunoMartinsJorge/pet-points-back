package br.com.api.petpoints.domain.users.cliente.features.meuspets.forms;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.enums.TipoAnimalEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class EditarPetForm {
    @NotEmpty(message = "O campo 'nome' não pode estar vazio!")
    @NotBlank(message = "O campo 'nome' não pode estar branco!")
    private String nome;
    private String observacoes;
    @NotNull(message = "O campo 'genêro' não pode ser nulo!")
    private GeneroEnum genero;
    @NotEmpty(message = "O campo 'raca' não pode estar vazio!")
    @NotBlank(message = "O campo 'raca' não pode estar branco!")
    private String raca;
    @NotNull(message = "O campo 'genêro' não pode ser nulo!")
    private TipoAnimalEnum tipoAnimal;
    @NotEmpty(message = "O campo 'dataNascimento' não pode estar vazio!")
    @NotBlank(message = "O campo 'dataNascimento' não pode estar branco!")
    @NotNull(message = "O campo 'dataNascimento' não pode ser nulo!")
    private LocalDate dataNascimento;
}
