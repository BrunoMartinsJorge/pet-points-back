package br.com.api.petpoints.domain.users.veterinario.features.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardsVeterinarioDashboardDto {

    private int consultasFinalizadas;
    private int totalConsultas;
    private int ranking;
}
