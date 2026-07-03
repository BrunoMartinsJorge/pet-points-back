package br.com.api.petpoints.domain.users.gerente.features.consultas.form;

import lombok.Getter;

@Getter
public class FiltroConsultaForm {

    private Long idCliente;
    private Long idVeterinario;
    private Long idTipoConsulta;
}
