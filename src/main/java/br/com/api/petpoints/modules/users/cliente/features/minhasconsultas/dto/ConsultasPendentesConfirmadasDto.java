package br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.dto;

import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.models.ConsultaModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConsultasPendentesConfirmadasDto {

    private Long id;
    private String petConsulta;
    private String veterinarioConsulta;
    private LocalDateTime dataHoraConsulta;
    private StatusConsultaEnum statusConsulta;
    private String tipoConsulta;

    public ConsultasPendentesConfirmadasDto(ConsultaModel consulta) {
        this.id = consulta.getId();
        this.petConsulta = consulta.getPet().getNome();
        this.veterinarioConsulta = consulta.getVeterinario().getNome();
        this.dataHoraConsulta = consulta.getDataConsulta();
        this.statusConsulta = consulta.getStatus();
        this.tipoConsulta = consulta.getTipoConsulta().getNome();
    }

    public static List<ConsultasPendentesConfirmadasDto> convert(List<ConsultaModel> consultas) {
        return consultas.stream().map(ConsultasPendentesConfirmadasDto::new).toList();
    }
}
