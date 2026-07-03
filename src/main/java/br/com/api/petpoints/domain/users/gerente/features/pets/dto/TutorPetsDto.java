package br.com.api.petpoints.domain.users.gerente.features.pets.dto;

import br.com.api.petpoints.shared.models.UsuarioModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TutorPetsDto {
    private Long id;
    private String nome;

    public TutorPetsDto(UsuarioModel usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
    }
}
