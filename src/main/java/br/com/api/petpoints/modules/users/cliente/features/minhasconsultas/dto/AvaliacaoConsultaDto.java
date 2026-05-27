package br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.dto;

import br.com.api.petpoints.shared.models.AvaliacaoModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvaliacaoConsultaDto {

    private int pontuacao;
    private String observacoes;

    public AvaliacaoConsultaDto(AvaliacaoModel avaliacao) {
        this.pontuacao = avaliacao.getPontuacao();
        this.observacoes = avaliacao.getObservacoes();
    }
}
