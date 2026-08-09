package br.com.api.petpoints.domain.users.atendente.features.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardsDashboardDto {

    private Long atendimentosFinalizados;
    private Long consultasParticipadas;
    private Long rankingAvaliacao;
}
