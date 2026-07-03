package br.com.api.petpoints.shared.features.perfil.dto;

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
public class ConsultasAtendenteVeterinarioDto {

    private Long id;
    private String cliente;
    private String pet;
    private StatusConsultaEnum status;
    private LocalDateTime dataHoraConsulta;
    private String observacoes;
    private String tipoConsulta;
    private String motivoCancelamento;
    private String motivoIndeferimento;

    public ConsultasAtendenteVeterinarioDto(ConsultaModel consulta) {
        this.id = consulta.getId();
        this.cliente = consulta.getSolicitante().getNome();
        this.pet = consulta.getSolicitante().getNome();
        this.status = consulta.getStatus();
        this.dataHoraConsulta = consulta.getDataConsulta();
        this.observacoes = consulta.getObservacoes();
        this.tipoConsulta = consulta.getTipoConsulta().getNome();
        this.motivoCancelamento = consulta.getMotivoCancelamento();
        this.motivoIndeferimento = consulta.getMotivoIndeferimento();
    }

    public static List<ConsultasAtendenteVeterinarioDto> convert(List<ConsultaModel> consultas) {
        return consultas.stream().map(ConsultasAtendenteVeterinarioDto::new).toList();
    }
}
