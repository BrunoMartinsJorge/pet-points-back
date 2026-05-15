package br.com.api.petpoints.modules.users.gerente.features.funcionarios.forms;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.shared.enums.GeneroEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Getter
public class FuncionarioForm {

    @Email
    private String email;
    @NotBlank(message = "O campo 'nome' não pode estar em branco!")
    private String nome;
    @NotBlank(message = "O campo 'senha' não pode estar em branco!")
    private String senha;
    @CPF
    @NotBlank(message = "O campo 'cpf' não pode estar em branco!")
    private String cpf;
    @NotBlank(message = "O campo 'telefone' não pode estar em branco!")
    private String telefone;
    @NotNull(message = "O campo 'genero' não pode estar nulo!")
    private GeneroEnum genero;
    private LocalDate dataNascimento;
    @NotNull(message = "O campo 'permissao' não pode estar nulo!")
    private TipoUsuario permissao;
    private Long especializacao;
}
