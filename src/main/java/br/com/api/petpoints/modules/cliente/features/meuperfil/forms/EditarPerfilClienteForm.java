package br.com.api.petpoints.modules.cliente.features.meuperfil.forms;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class EditarPerfilClienteForm {
    private String email;
    private String nome;
    private String cpf;
    private String telefone;
    private GeneroEnum genero;
    private LocalDate dataNascimento;
}
