package br.com.api.petpoints.domain.users.atendente.features.pagamentos.dto;

import br.com.api.petpoints.shared.models.UsuarioModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponsavelPagamentoDto {

    private String nome;
    private String email;
    private String cargo;
    private LocalDateTime data;

    public ResponsavelPagamentoDto(UsuarioModel usuario, LocalDateTime data) {
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.cargo = formatarCargo(usuario);
        this.data = data;
    }

    /**
     * A descrição da permissão é armazenada em caixa alta (ex.: "ATENDENTE").
     * Aqui ela é normalizada para exibição ("Atendente").
     */
    private static String formatarCargo(UsuarioModel usuario) {
        if (usuario.getPermissao() == null) return null;
        String descricao = usuario.getPermissao().getDescricao();
        if (descricao == null || descricao.isBlank()) return null;
        return descricao.charAt(0) + descricao.substring(1).toLowerCase();
    }
}
