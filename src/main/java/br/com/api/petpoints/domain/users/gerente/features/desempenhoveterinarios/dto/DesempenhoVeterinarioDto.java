package br.com.api.petpoints.domain.users.gerente.features.desempenhoveterinarios.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DesempenhoVeterinarioDto {

    private Long id;
    private String nome;
    private String especializacoes;
    private int totalConsultas;
    private int finalizadas;
    private int canceladas;
    private int reprovadas;
    private double taxaConclusao;
    private Double notaMedia;
    private int quantidadeAvaliacoes;
    private Double tempoMedioAtendimentoMinutos;
}
