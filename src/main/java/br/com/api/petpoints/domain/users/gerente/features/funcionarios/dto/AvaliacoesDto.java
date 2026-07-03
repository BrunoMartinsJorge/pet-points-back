package br.com.api.petpoints.domain.users.gerente.features.funcionarios.dto;

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
public class AvaliacoesDto {

    private Long id;
    private String mensagem;
    private double pontuacao;
    private String remetente;
    private String dataAvaliacao;

    public AvaliacoesDto(AvaliacaoModel avaliacao) {
        this.id = avaliacao.getId();
        this.mensagem = avaliacao.getObservacoes();
        this.pontuacao = avaliacao.getPontuacao();
        this.remetente = avaliacao.getAvaliadoPor().getNome();
        this.dataAvaliacao = avaliacao.getAvaliadoEm() != null ? LocalDateTimeUtils.converterLocalDateTimeParaPtBr(avaliacao.getAvaliadoEm()) : null;
    }

    public static List<AvaliacoesDto> convert(List<AvaliacaoModel> avaliacoes) {
        return avaliacoes.stream().map(AvaliacoesDto::new).toList();
    }
}
