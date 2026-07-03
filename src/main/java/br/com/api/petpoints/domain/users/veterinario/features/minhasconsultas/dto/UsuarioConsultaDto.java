package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.enums.StatusPerfilEnum;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import br.com.api.petpoints.shared.utils.LocalDateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioConsultaDto {

    private Long id;
    private String email;
    private String nome;
    private String cpf;
    private String telefone;
    private GeneroEnum genero;
    private String dataNascimento;
    private String dataCadastro;
    private StatusPerfilEnum statusPerfilEnum;

    public UsuarioConsultaDto(UsuarioModel usuario) {
        this.id = usuario.getId();
        this.email = usuario.getEmail();
        this.nome = usuario.getNome();
        this.cpf = usuario.getCpf();
        this.telefone = usuario.getTelefone();
        this.genero = usuario.getGenero();
        this.dataNascimento = LocalDateUtils.converterLocalDateParaPtBr(usuario.getDataNascimento());
        this.dataCadastro = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(usuario.getDataCadastro());
    }
}
