package br.com.api.petpoints.domain.users.cliente.features.meuperfil.forms;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import lombok.Getter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Getter
public class EditarPerfilClienteForm {
    private String email;
    private String nome;
    @CPF
    private String cpf;
    private String telefone;
    private GeneroEnum genero;
    private LocalDate dataNascimento;
}
