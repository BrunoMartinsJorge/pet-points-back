package br.com.api.petpoints.domain.users.cliente.features.meuspets.dto;

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
public class ConsultasPendentesIniciadasDto {

    private Long id;
    private String dataConsulta;
    private StatusConsultaEnum status;
    private String tipoConsulta;

    public ConsultasPendentesIniciadasDto(ConsultaModel consulta) {
        this.id = consulta.getId();
        this.dataConsulta = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getDataConsulta());
        this.status = consulta.getStatus();
        this.tipoConsulta = consulta.getTipoConsulta().getNome();
    }

    public static List<ConsultasPendentesIniciadasDto> convert(List<ConsultaModel> consultas) {
        return consultas.stream().map(ConsultasPendentesIniciadasDto::new).toList();
    }
}
