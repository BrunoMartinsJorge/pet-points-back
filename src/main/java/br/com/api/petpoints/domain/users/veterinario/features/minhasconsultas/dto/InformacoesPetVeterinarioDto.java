package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.enums.TipoAnimalEnum;
import br.com.api.petpoints.shared.models.PetModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InformacoesPetVeterinarioDto {

    private String nome;
    private GeneroEnum genero;
    private TipoAnimalEnum tipo;
    private String raca;
    private String idade;
    private String nomeTutor;

    public InformacoesPetVeterinarioDto(PetModel pet) {
        this.nome = pet.getNome();
        this.genero = pet.getGenero();
        this.tipo = pet.getTipo();
        this.raca = pet.getRaca();
        this.idade = LocalDateTime.now().getYear() - pet.getDataNascimento().getYear() + " Anos.";
        this.nomeTutor = pet.getTutor().getNome();
    }
}
