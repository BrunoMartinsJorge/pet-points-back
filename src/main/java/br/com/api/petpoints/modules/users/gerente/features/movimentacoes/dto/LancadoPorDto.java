package br.com.api.petpoints.modules.users.gerente.features.movimentacoes.dto;

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
public class LancadoPorDto {

    private Long id;
    private String nome;

    public LancadoPorDto(UsuarioModel usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
    }

    public static List<LancadoPorDto> convert(List<UsuarioModel> usuarios) {
        return usuarios.stream().map(LancadoPorDto::new).toList();
    }
}
