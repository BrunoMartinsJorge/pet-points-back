package br.com.api.petpoints.domain.users.gerente.features.consultas.dto;

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
public class ConsultaClinicaDto {

    private Long id;
    private TipoConsultaAtributoDto tipo;
    private StatusConsultaEnum status;
    private LocalDateTime solicitadoEm;
    private String observacoes;
    private ParticipantesConsultaDto cliente;
    private ParticipantesConsultaDto veterinario;

    public ConsultaClinicaDto(ConsultaModel consulta) {
        this.id = consulta.getId();
        this.tipo = new TipoConsultaAtributoDto(consulta.getTipoConsulta());
        this.status = consulta.getStatus();
        this.solicitadoEm = consulta.getSolicitadoEm();
        this.observacoes = consulta.getObservacoes();
        this.cliente = new ParticipantesConsultaDto(consulta.getSolicitante());
        this.veterinario = new ParticipantesConsultaDto(consulta.getVeterinario());
    }

    public static List<ConsultaClinicaDto> convert(List<ConsultaModel> consultas) {
        return consultas.stream().map(ConsultaClinicaDto::new).toList();
    }
}
