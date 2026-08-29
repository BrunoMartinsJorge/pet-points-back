package br.com.api.petpoints.shared.form;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistroForm {
    @NotBlank(message = "O campo 'nome' não pode estar em branco!")
    @Length(
            min = 8,
            max = 156,
            message = "O campo 'nome' deve conter entre '8' e '156' caracteres!"
    )
    private String nome;

    @NotNull(message = "O campo 'genero' não pode ser nulo!")
    private GeneroEnum genero;

    @CPF(message = "CPF inválido!")
    private String cpf;

    @Email(message = "O valor do campo 'email' não corresponde a um email!")
    private String email;

    @NotBlank(message = "O campo 'telefone' não pode estar em branco!")
    private String telefone;

    @NotBlank(message = "O campo 'senha' não pode estar em branco!")
    @Length(
            min = 6,
            max = 100,
            message = "O campo 'senha' deve conter entre '6' e '100' caracteres!"
    )
    private String senha;

    @NotNull(message = "O campo 'dataNascimento' não pode ser nulo!")
    @Past(message = "A data de nascimento deve ser uma data no passado!")
    private LocalDate dataNascimento;
}
