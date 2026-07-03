package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.enums.TipoAnimalEnum;
import br.com.api.petpoints.shared.models.PetModel;
import br.com.api.petpoints.shared.utils.LocalDateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PetConsultaDto {

    private Long id;
    private String nome;
    private TipoAnimalEnum tipo;
    private String raca;
    private GeneroEnum genero;
    private String dataNascimento;
    private String observacoes;

    public PetConsultaDto(PetModel pet) {
        this.id = pet.getId();
        this.nome = pet.getNome();
        this.tipo = pet.getTipo();
        this.raca = pet.getRaca();
        this.genero = pet.getGenero();
        this.dataNascimento = LocalDateUtils.converterLocalDateParaPtBr(pet.getDataNascimento());
        this.observacoes = pet.getObservacoes();
    }
}
