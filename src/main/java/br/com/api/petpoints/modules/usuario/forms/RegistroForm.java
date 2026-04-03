package br.com.api.petpoints.modules.usuario.forms;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import lombok.Getter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Getter
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
