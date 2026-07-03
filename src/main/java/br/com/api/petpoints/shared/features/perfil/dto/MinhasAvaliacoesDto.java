package br.com.api.petpoints.shared.features.perfil.dto;

import br.com.api.petpoints.shared.models.AvaliacaoModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MinhasAvaliacoesDto {

    private Long id;
    private String mensagem;
    private int pontuacao;
    private String tipo;
    private LocalDateTime dataAvaliacao;

    public MinhasAvaliacoesDto(AvaliacaoModel avaliacao, String tipo) {
        this.id = avaliacao.getId();
        this.mensagem = avaliacao.getObservacoes();
        this.pontuacao = avaliacao.getPontuacao();
        this.tipo = tipo;
        this.dataAvaliacao = avaliacao.getAvaliadoEm();
    }
}
