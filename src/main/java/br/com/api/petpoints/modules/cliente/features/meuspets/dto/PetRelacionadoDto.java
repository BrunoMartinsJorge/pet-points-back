package br.com.api.petpoints.modules.cliente.features.meuspets.dto;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.enums.TipoAnimalEnum;
import br.com.api.petpoints.shared.models.PetModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PetRelacionadoDto {

    private Long id;
    private String nome;
    private GeneroEnum genero;
    private TipoAnimalEnum tipo;
    private UUID arquivo;

    public PetRelacionadoDto(PetModel pet) {
        this.id = pet.getId();
        this.nome = pet.getNome();
        this.genero = pet.getGenero();
        this.tipo = pet.getTipo();
        this.arquivo = pet.getImagem();
    }

    public static List<PetRelacionadoDto> convert(List<PetModel> pets) {
        return pets.stream().map(PetRelacionadoDto::new).toList();
    }
}
