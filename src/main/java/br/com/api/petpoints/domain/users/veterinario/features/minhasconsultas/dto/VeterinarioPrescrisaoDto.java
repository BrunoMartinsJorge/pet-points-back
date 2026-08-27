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
public class VeterinarioPrescrisaoDto {
    private String nome;
    private String crmv;

    public VeterinarioPrescrisaoDto(UsuarioModel veterinario) {
        this.nome = veterinario.getNome();
        this.crmv = veterinario.getCrmv();
    }
}
