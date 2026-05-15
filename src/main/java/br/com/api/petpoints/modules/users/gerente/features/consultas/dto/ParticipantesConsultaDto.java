package br.com.api.petpoints.modules.users.gerente.features.consultas.dto;

import br.com.api.petpoints.shared.models.UsuarioModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantesConsultaDto {

    private Long id;
    private String nome;

    public ParticipantesConsultaDto(UsuarioModel usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
    }
}
