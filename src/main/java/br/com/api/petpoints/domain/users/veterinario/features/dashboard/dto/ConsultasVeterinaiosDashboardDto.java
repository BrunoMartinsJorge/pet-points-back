package br.com.api.petpoints.domain.users.veterinario.features.dashboard.dto;

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
public class ConsultasVeterinaiosDashboardDto {

    private Long id;
    private String tipo;
    private LocalDateTime data;
    private String cliente;
    private String pet;
    private StatusConsultaEnum status;
    private String observacoes;

    public ConsultasVeterinaiosDashboardDto(ConsultaModel consulta) {
        this.id = consulta.getId();
        this.tipo = consulta.getTipoConsulta().getNome();
        this.data = consulta.getDataConsulta();
        this.cliente = consulta.getSolicitante().getNome();
        this.pet = consulta.getSolicitante().getNome();
        this.status = consulta.getStatus();
        this.observacoes = consulta.getObservacoes();
    }

    public static List<ConsultasVeterinaiosDashboardDto> convert(List<ConsultaModel> consultas) {
        return consultas.stream().map(ConsultasVeterinaiosDashboardDto::new).toList();
    }
}
