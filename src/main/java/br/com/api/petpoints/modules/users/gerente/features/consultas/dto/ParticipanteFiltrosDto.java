package br.com.api.petpoints.modules.users.gerente.features.consultas.dto;

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
public class ParticipanteFiltrosDto {

    private Long value;
    private String label;

    public ParticipanteFiltrosDto(UsuarioModel usuario) {
        this.value = usuario.getId();
        this.label = usuario.getNome();
    }

    public static List<ParticipanteFiltrosDto> convert(List<UsuarioModel> usuarios) {
        return usuarios.stream().map(ParticipanteFiltrosDto::new).toList();
    }
}
