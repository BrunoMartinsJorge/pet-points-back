package br.com.api.petpoints.shared.features.perfil.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RankingFuncionarioDto {

    private Integer classificacao;
    private int pontuacao;
}
