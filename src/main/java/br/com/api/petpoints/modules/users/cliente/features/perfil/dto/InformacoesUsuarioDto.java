package br.com.api.petpoints.modules.users.cliente.features.perfil.dto;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.utils.LocalDateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InformacoesUsuarioDto {
    private String nome;
    private String dataNascimento;
    private GeneroEnum genero;
    private String email;
    private String telefone;
    private String cpf;
    private UUID imagem;

    public InformacoesUsuarioDto(UsuarioModel usuario) {
        this.nome = usuario.getNome();
        this.dataNascimento = LocalDateUtils.converterLocalDateParaPtBr(usuario.getDataNascimento());
        this.genero = usuario.getGenero();
        this.email = usuario.getEmail();
        this.telefone = usuario.getTelefone();
        this.cpf = usuario.getCpf();
        this.imagem = usuario.getImagem();
    }
}
