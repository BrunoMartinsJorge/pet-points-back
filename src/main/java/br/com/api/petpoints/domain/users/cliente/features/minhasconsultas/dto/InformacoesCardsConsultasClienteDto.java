package br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InformacoesCardsConsultasClienteDto {

    private long consultasFinalizadas;
    private long consultasPendentes;
    private long consultasIndeferidasCanceladas;
}
