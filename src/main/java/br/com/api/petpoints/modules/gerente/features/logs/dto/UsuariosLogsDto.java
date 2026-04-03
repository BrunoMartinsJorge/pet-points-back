package br.com.api.petpoints.modules.gerente.features.logs.dto;

import br.com.api.petpoints.shared.models.UsuarioModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuariosLogsDto {

    private Long idUsuario;
    private String nome;

    public UsuariosLogsDto(UsuarioModel usuario) {
        this.idUsuario = usuario.getId();
        this.nome = usuario.getNome();
    }
}
