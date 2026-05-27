package br.com.api.petpoints.modules.users.cliente.features.meuspets.forms;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.enums.TipoAnimalEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class NovoPetForm {
    private String nome;
    private TipoAnimalEnum tipo;
    private String raca;
    private GeneroEnum genero;
    private LocalDate dataNascimento;
    private String observacoes;
}
