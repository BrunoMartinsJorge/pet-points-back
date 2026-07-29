package br.com.api.petpoints.domain.users.gerente.features.financeiro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GraficoReceitaDto {

    private List<String> labels;
    private List<Double> valores;
}
