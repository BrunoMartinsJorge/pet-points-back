package br.com.api.petpoints.domain.users.gerente.features.desempenhoveterinarios.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResumoDesempenhoVeterinariosDto {

    private int totalConsultas;
    private int totalFinalizadas;
    private int totalCanceladas;
    private int totalReprovadas;
    private double taxaConclusaoGeral;
    private double taxaCancelamentoGeral;
    private double taxaReprovacaoGeral;
    private Double notaMediaGeral;
    private int totalAvaliacoes;
    private String veterinarioDestaque;
    private Double notaVeterinarioDestaque;
}
