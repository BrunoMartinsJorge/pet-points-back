package br.com.api.petpoints.modules.auth.forms;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegistroForm {
    private String nome;
    private GeneroEnum genero;
    @CPF(message = "CPF inválido!")
    private String cpf;
    private String email;
    private String telefone;
    private String senha;
    private LocalDate dataNascimento;
}
