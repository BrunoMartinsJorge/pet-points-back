package br.com.api.petpoints.modules.usuario.forms;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class RegistroForm {
    private String nome;
    private GeneroEnum genero;
    @Pattern(regexp = "[0-9]{3}\\\\.[0-9]{3}\\\\.[0-9]{3}-[0-9]{2}", message = "O campo 'CPF' está com um valor inválido para um CPF!")
    private String cpf;
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$\n" +
            "\n", message = "Campo 'email' com valor inválido para email!")
    private String email;
    private String telefone;
    private String senha;
    private LocalDate dataNascimento;
}
