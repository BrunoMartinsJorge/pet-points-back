package br.com.api.petpoints.domain.users.cliente.features.dashboard.dto;

import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.models.ConsultaModel;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConsultaDashboardDto {

    private Long id;
    private String dataConsulta;
    private StatusConsultaEnum status;
    private String pet;
    private String veterinario;
    private String tipoConsulta;
    private String dataSolicitacao;

    public ConsultaDashboardDto(ConsultaModel consulta) {
        this.id = consulta.getId();
        this.dataConsulta = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getDataConsulta());
        this.status = consulta.getStatus();
        this.pet = consulta.getPet().getNome();
        this.veterinario = consulta.getVeterinario().getNome();
        this.tipoConsulta = consulta.getTipoConsulta().getNome();
        this.dataSolicitacao = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getSolicitadoEm());
    }

    public static List<ConsultaDashboardDto> convert(List<ConsultaModel> consultas) {
        return consultas.stream().map(ConsultaDashboardDto::new).toList();
    }
}
