package br.com.api.petpoints.shared.features.chatatendimento.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardsAtendimentoAtendenteDto {

    private long atendimentosEmAndamento;
    private long atendimentosFinalizadas;
    private BigDecimal pontuacaoMedia;
}
