package br.com.api.petpoints.shared.form;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Getter
@Setter
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
