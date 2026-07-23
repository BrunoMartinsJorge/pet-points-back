package br.com.api.petpoints.shared.features.chatinterno.dto;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.shared.models.UsuarioModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParticipanteChatInternoDto {

    private Long id;
    private String nome;
    private TipoUsuario tipoUsuario;

    public ParticipanteChatInternoDto(UsuarioModel usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.tipoUsuario = usuario.getPermissao();
    }
}
