package br.com.api.petpoints.shared.features.chatatendimento.dto;

import br.com.api.petpoints.shared.models.UsuarioModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Atendente exibido na central de atendimento do cliente, apenas para dar rosto
 * à equipe que vai responder. Não expõe nada além de id e nome.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EquipeAtendimentoDto {

    private Long id;
    private String nome;

    public EquipeAtendimentoDto(UsuarioModel atendente) {
        this.id = atendente.getId();
        this.nome = atendente.getNome();
    }

    public static List<EquipeAtendimentoDto> convert(List<UsuarioModel> atendentes) {
        return atendentes.stream().map(EquipeAtendimentoDto::new).toList();
    }
}
