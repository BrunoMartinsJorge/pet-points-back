package br.com.api.petpoints.shared.features.clientes.dto;

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
public class HistoricoConsultasClienteDto {

    private Long id;
    private String tipoConsulta;
    private StatusConsultaEnum status;
    private LocalDateTime deferidoEm;
    private LocalDateTime solicitadoEm;
    private LocalDateTime dataHoraConsulta;
    private String descricao;
    private String veterinario;
    private String motivoIndeferimento;
    private String motivoCancelamento;
    private String pet;

    public HistoricoConsultasClienteDto(ConsultaModel consulta) {
        this.id = consulta.getId();
        this.tipoConsulta = consulta.getTipoConsulta().getNome();
        this.status = consulta.getStatus();
        this.deferidoEm = consulta.getDeferidoEm();
        this.solicitadoEm = consulta.getSolicitadoEm();
        this.dataHoraConsulta = consulta.getDataConsulta();
        this.descricao = consulta.getObservacoes();
        this.veterinario = consulta.getVeterinario().getNome();
        this.motivoIndeferimento = consulta.getMotivoIndeferimento();
        this.motivoCancelamento = consulta.getMotivoIndeferimento();
        this.pet = consulta.getPet().getNome();
    }

    public static List<HistoricoConsultasClienteDto> convert(List<ConsultaModel> consultas) {
        return consultas.stream().map(HistoricoConsultasClienteDto::new).toList();
    }
}
