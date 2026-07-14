package br.com.api.petpoints.domain.users.veterinario.features.dashboard.dto;

import br.com.api.petpoints.shared.models.AvaliacaoModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvaliacoesVeterinarioDashboardDto {

    private Long id;
    private String cliente;
    private int pontuacao;
    private String observacoes;
    private LocalDateTime avaliadoEm;

    public AvaliacoesVeterinarioDashboardDto(AvaliacaoModel avaliacao) {
        this.id = avaliacao.getId();
        this.cliente = avaliacao.getAvaliadoPor().getNome();
        this.pontuacao = avaliacao.getPontuacao();
        this.observacoes = avaliacao.getObservacoes();
        this.avaliadoEm = avaliacao.getAvaliadoEm();
    }

    public static List<AvaliacoesVeterinarioDashboardDto> convert(List<AvaliacaoModel> avaliacoes) {
        return avaliacoes.stream().map(AvaliacoesVeterinarioDashboardDto::new).toList();
    }
}
