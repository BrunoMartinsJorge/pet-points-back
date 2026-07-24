package br.com.api.petpoints.domain.users.gerente.features.desempenhoveterinarios.form;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class RelatorioDesempenhoVeterinariosForm {

    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Long idVeterinario;
}
