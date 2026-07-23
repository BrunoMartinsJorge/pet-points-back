package br.com.api.petpoints.domain.users.gerente.features.consultas.dto;

import br.com.api.petpoints.shared.models.EspecializacaoModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EspecializacoesDto {

    private Long id;
    private String descricao;

    public EspecializacoesDto(EspecializacaoModel especializacao) {
        this.id = especializacao.getId();
        this.descricao = especializacao.getDescricao();
    }

    public static List<EspecializacoesDto> convert(List<EspecializacaoModel> especializacoes) {
        return especializacoes.stream().map(EspecializacoesDto::new).toList();
    }
}
