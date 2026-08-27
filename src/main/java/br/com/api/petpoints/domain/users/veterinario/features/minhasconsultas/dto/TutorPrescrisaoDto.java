package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto;

import br.com.api.petpoints.shared.models.UsuarioModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TutorPrescrisaoDto {
    private String nome;
    private String cpf;
    private String telefone;

    public TutorPrescrisaoDto(UsuarioModel tutor) {
        this.nome = tutor.getNome();
        this.cpf = tutor.getCpf();
        this.telefone = tutor.getTelefone();
    }
}
