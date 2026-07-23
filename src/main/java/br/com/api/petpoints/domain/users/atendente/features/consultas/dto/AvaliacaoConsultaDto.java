package br.com.api.petpoints.domain.users.atendente.features.consultas.dto;

import br.com.api.petpoints.shared.models.AvaliacaoModel;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvaliacaoConsultaDto {

    private Long id;
    private int pontuacao;
    private String observacoes;
    private String enviadoEm;

    public AvaliacaoConsultaDto(AvaliacaoModel avaliacao) {
        this.id = avaliacao.getId();
        this.pontuacao = avaliacao.getPontuacao();
        this.observacoes = avaliacao.getObservacoes();
        this.enviadoEm = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(avaliacao.getAvaliadoEm());
    }

    public static List<AvaliacaoConsultaDto> convert(List<AvaliacaoModel> avaliacoes) {
        return avaliacoes.stream().map(AvaliacaoConsultaDto::new).toList();
    }
}
