package br.com.api.petpoints.modules.cliente.features.meuspets.dto;

import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.models.*;
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
public class MeuPetConsultaDto {
    private Long id;
    private StatusConsultaEnum status;
    private String pet;
    private String tipoConsulta;
    private String solicitadoEm;
    private String dataConsulta;

    public MeuPetConsultaDto(ConsultaModel consulta) {
        this.id = consulta.getId();
        this.status = consulta.getStatus();
        this.pet = consulta.getPet().getNome();
        this.tipoConsulta = consulta.getTipoConsulta().getNome();
        this.solicitadoEm = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getSolicitadoEm());
        this.dataConsulta = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getDataConsulta());
    }

    public static List<MeuPetConsultaDto> convert(List<ConsultaModel> consultas) {
        return consultas.stream().map(MeuPetConsultaDto::new).toList();
    }
}
