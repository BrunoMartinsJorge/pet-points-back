package br.com.api.petpoints.modules.users.gerente.features.consultas.dto;

import br.com.api.petpoints.shared.models.EspecializacaoModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetalhesEspecialziacaoDto {

    private Long id;
    private String nome;
    private List<ParticipantesConsultaDto> veterinarios;
    private List<ParticipantesConsultaDto> veterinariosNaoRelacionados;

    public DetalhesEspecialziacaoDto(EspecializacaoModel especializacao, List<UsuarioModel> veterinariosNaoRelacionados) {
        this.id = especializacao.getId();
        this.nome = especializacao.getDescricao();
        this.veterinarios = especializacao.getVeterinarios().stream().map(ParticipantesConsultaDto::new).toList();
        this.veterinariosNaoRelacionados = veterinariosNaoRelacionados.stream().map(ParticipantesConsultaDto::new).toList();
    }
}
