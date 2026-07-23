package br.com.api.petpoints.domain.users.gerente.features.pets.dto;

import br.com.api.petpoints.shared.models.UsuarioModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TutorDto {

    private Long value;
    private String label;

    public TutorDto(UsuarioModel usuario) {
        this.value = usuario.getId();
        this.label = usuario.getNome();
    }

    public static List<TutorDto> convert(List<UsuarioModel> usuarios) {
        return usuarios.stream().map(TutorDto::new).toList();
    }
}
