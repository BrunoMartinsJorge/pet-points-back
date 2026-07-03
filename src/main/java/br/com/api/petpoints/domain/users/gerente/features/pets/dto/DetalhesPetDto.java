package br.com.api.petpoints.domain.users.gerente.features.pets.dto;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.enums.TipoAnimalEnum;
import br.com.api.petpoints.shared.models.PetModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetalhesPetDto {
    private Long id;
    private String nome;
    private GeneroEnum genero;
    private TipoAnimalEnum tipo;
    private LocalDate dataNascimento;
    private LocalDateTime registradoEm;
    private String raca;
    private String observacoes;

    public DetalhesPetDto(PetModel pet) {
        this.id = pet.getId();
        this.nome = pet.getNome();
        this.genero = pet.getGenero();
        this.tipo = pet.getTipo();
        this.dataNascimento = pet.getDataNascimento();
        this.registradoEm = pet.getRegistradoEm();
        this.raca = pet.getRaca();
        this.observacoes = pet.getObservacoes();
    }
}
