package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.enums.TipoAnimalEnum;
import br.com.api.petpoints.shared.models.PetModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InformacoesPetConsultaSelecionadaDto {

    private Long id;
    private UUID imagem;
    private String nome;
    private GeneroEnum genero;
    private TipoAnimalEnum tipo;
    private String raca;
    private LocalDate dataNascimento;
    private LocalDateTime registradoEm;
    private String problemasSaude;

    public InformacoesPetConsultaSelecionadaDto(PetModel pet) {
        this.id = pet.getId();
        this.imagem = pet.getImagem();
        this.nome = pet.getNome();
        this.genero = pet.getGenero();
        this.tipo = pet.getTipo();
        this.raca = pet.getRaca();
        this.dataNascimento = pet.getDataNascimento();
        this.registradoEm = pet.getRegistradoEm();
        this.problemasSaude = pet.getObservacoes();
    }
}
