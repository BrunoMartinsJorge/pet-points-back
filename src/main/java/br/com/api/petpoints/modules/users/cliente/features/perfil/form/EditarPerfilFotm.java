package br.com.api.petpoints.modules.users.cliente.features.perfil.form;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class EditarPerfilFotm {
    private String nome;
    private LocalDate dataNascimento;
    private GeneroEnum genero;
    private String email;
    private String telefone;
    private String cpf;
}
