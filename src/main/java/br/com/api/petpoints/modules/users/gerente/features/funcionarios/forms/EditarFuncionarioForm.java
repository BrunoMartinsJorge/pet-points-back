package br.com.api.petpoints.modules.users.gerente.features.funcionarios.forms;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.shared.enums.GeneroEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class EditarFuncionarioForm {
    private String nome;
    private GeneroEnum genero;
    private String email;
    private String telefone;
    private TipoUsuario permissao;
    private LocalDate dataNascimento;
}
