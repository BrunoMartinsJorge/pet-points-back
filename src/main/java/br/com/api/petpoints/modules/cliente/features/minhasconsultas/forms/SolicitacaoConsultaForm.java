package br.com.api.petpoints.modules.cliente.features.minhasconsultas.forms;

import br.com.api.petpoints.shared.models.TipoConsultaModel;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SolicitacaoConsultaForm {

    private Long idPet;
    private Long idVeterinario;
    private Long idTipoConsulta;
    private LocalDateTime dataConsulta;
    private String observacoes;
}
