package br.com.api.petpoints.modules.users.gerente.features.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AcessosMesDto {

    private String data;
    private int quantidade;
}
