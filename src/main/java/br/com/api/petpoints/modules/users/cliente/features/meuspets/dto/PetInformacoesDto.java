package br.com.api.petpoints.modules.users.cliente.features.meuspets.dto;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.enums.StatusPerfilEnum;
import br.com.api.petpoints.shared.enums.TipoAnimalEnum;
import br.com.api.petpoints.shared.models.PetModel;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import br.com.api.petpoints.shared.utils.LocalDateUtils;
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
public class PetInformacoesDto {
    private String nome;
    private String raca;
    private String dataNascimento;
    private String dataRegistro;
    private TipoAnimalEnum tipo;
    private GeneroEnum genero;
    private UUID imagem;
    private String observacoes;
    private List<PetRelacionadoDto> petsRelacionados;
    private StatusPerfilEnum status;

    public PetInformacoesDto(PetModel pet, List<PetModel> petsRelacionados) {
        this.nome = pet.getNome();
        this.raca = pet.getRaca();
        this.dataNascimento = LocalDateUtils.converterLocalDateParaPtBr(pet.getDataNascimento());
        this.dataRegistro = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(pet.getRegistradoEm());
        this.tipo = pet.getTipo();
        this.genero = pet.getGenero();
        this.imagem = pet.getImagem();
        this.observacoes = pet.getObservacoes();
        this.petsRelacionados = PetRelacionadoDto.convert(petsRelacionados);
        this.status = pet.getStatus();
    }
}
