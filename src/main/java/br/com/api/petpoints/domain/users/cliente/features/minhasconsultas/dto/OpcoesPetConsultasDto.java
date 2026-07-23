package br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto;

import br.com.api.petpoints.shared.models.PetModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OpcoesPetConsultasDto {

    private Long value;
    private String label;

    public OpcoesPetConsultasDto(PetModel pet){
        this.value = pet.getId();
        this.label = pet.getNome();
    }

    public static List<OpcoesPetConsultasDto> convert(List<PetModel> pets){
        return pets.stream().map(OpcoesPetConsultasDto::new).toList();
    }
}
