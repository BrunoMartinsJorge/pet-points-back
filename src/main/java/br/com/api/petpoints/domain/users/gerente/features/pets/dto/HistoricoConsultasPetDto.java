package br.com.api.petpoints.domain.users.gerente.features.pets.dto;

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
public class HistoricoConsultasPetDto {

    private Long id;
    private String veterinario;
    private StatusConsultaEnum status;
    private LocalDateTime dataHoraConsulta;
    private String tipoConsulta;

    public HistoricoConsultasPetDto(ConsultaModel consulta) {
        this.id = consulta.getId();
        this.veterinario = consulta.getVeterinario().getNome();
        this.status = consulta.getStatus();
        this.dataHoraConsulta = consulta.getDataConsulta();
        this.tipoConsulta = consulta.getTipoConsulta().getNome();
    }

    public static List<HistoricoConsultasPetDto> convert(List<ConsultaModel> consultas) {
        return consultas.stream().map(HistoricoConsultasPetDto::new).toList();
    }
}
