package br.com.api.petpoints.shared.features.chatinterno.dto;

import br.com.api.petpoints.core.token.TipoUsuario;
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
public class UsuariosInternosDto {

    private Long id;
    private String nome;
    private TipoUsuario tipoUsuario;
    private Long idChat;

    public UsuariosInternosDto(UsuarioModel usuario, Long idChat) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.tipoUsuario = usuario.getPermissao();
        this.idChat = idChat;
    }

    /*public static List<UsuariosInternosDto> converter(List<UsuarioModel> usuarios) {
        return usuarios.stream().map(UsuariosInternosDto::new).toList();
    }*/
}
