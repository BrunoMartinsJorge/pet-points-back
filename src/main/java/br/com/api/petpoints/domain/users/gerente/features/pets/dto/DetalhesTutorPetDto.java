package br.com.api.petpoints.domain.users.gerente.features.pets.dto;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.models.UsuarioModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetalhesTutorPetDto {

    private Long id;
    private String nome;
    private String email;
    private GeneroEnum genero;
    private String telefone;

    public DetalhesTutorPetDto(UsuarioModel usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.genero = usuario.getGenero();
        this.telefone = usuario.getTelefone();
    }
}
