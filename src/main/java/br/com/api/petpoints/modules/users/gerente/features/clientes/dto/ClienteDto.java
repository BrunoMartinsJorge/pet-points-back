package br.com.api.petpoints.modules.users.gerente.features.clientes.dto;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDto {

    private Long id;
    private String nome;
    private String telefone;
    private GeneroEnum genero;
    private LocalDateTime registradoEm;

    public ClienteDto(UsuarioModel usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.telefone = usuario.getTelefone();
        this.genero = usuario.getGenero();
        this.registradoEm = usuario.getDataCadastro();
    }

    public static List<ClienteDto> convert(List<UsuarioModel> usuarios) {
        return usuarios.stream().map(ClienteDto::new).toList();
    }
}
