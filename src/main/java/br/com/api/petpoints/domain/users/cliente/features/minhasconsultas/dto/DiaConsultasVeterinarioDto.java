package br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiaConsultasVeterinarioDto {

    private LocalDate dia;
    private List<LocalTime> horariosPreenchidos;
}
