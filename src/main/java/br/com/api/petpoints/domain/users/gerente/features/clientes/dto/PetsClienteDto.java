package br.com.api.petpoints.domain.users.gerente.features.clientes.dto;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.enums.TipoAnimalEnum;
import br.com.api.petpoints.shared.models.PetModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PetsClienteDto {

    private Long id;
    private String nome;
    private TipoAnimalEnum tipo;
    private GeneroEnum genero;
    private LocalDate dataNascimento;
    private LocalDateTime registradoEm;

    public PetsClienteDto(PetModel pet) {
        this.id = pet.getId();
        this.nome = pet.getNome();
        this.tipo = pet.getTipo();
        this.genero = pet.getGenero();
        this.dataNascimento = pet.getDataNascimento();
        this.registradoEm = pet.getRegistradoEm();
    }

    public static List<PetsClienteDto> convert(List<PetModel> pets) {
        return pets.stream().map(PetsClienteDto::new).toList();
    }
}
