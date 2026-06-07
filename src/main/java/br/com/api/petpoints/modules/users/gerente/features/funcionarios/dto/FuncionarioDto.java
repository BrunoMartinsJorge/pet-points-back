package br.com.api.petpoints.modules.users.gerente.features.funcionarios.dto;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import br.com.api.petpoints.shared.utils.LocalDateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FuncionarioDto {

    private Long id;
    private String email;
    private String nome;
    private String cpf;
    private String telefone;
    private GeneroEnum genero;
    private LocalDate dataNascimento;
    private String dataCadastro;
    private String permissao;
    private String statusPerfil;
    private UUID arquivo;

    public FuncionarioDto(UsuarioModel usuario) {
        this.id = usuario.getId();
        this.email = usuario.getEmail();
        this.nome = usuario.getNome();
        this.cpf = usuario.getCpf();
        this.telefone = usuario.getTelefone();
        this.genero = usuario.getGenero();
        this.dataNascimento = usuario.getDataNascimento();
        this.dataCadastro = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(usuario.getDataCadastro());
        this.permissao = usuario.getPermissao().getDescricao();
        this.statusPerfil = usuario.getStatusPerfilEnum().getDescricao();
        this.arquivo = usuario.getImagem();
    }
}
