package br.com.api.petpoints.modules.users.cliente.features.meuspets.dto;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.enums.StatusPerfilEnum;
import br.com.api.petpoints.shared.enums.TipoAnimalEnum;
import br.com.api.petpoints.shared.models.PetModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MeuPetDto {

    private Long id;
    private String nome;
    private String tipo;
    private String raca;
    private GeneroEnum genero;
    private LocalDate dataNascimento;
    private String observacoes;
    private UUID imagem;
    private StatusPerfilEnum status;

    public MeuPetDto(PetModel pet) {
        this.id = pet.getId();
        this.nome = pet.getNome();
        this.tipo = pet.getTipo().getDescricao();
        this.raca = pet.getRaca();
        this.genero = pet.getGenero();
        this.dataNascimento = pet.getDataNascimento();
        this.observacoes = pet.getObservacoes();
        this.imagem = pet.getImagem();
        this.status = pet.getStatus();
    }
}
