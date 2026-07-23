package br.com.api.petpoints.domain.users.gerente.features.consultas.dto;

import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.models.ConsultaModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetalhesConsultaDto {

    private Long id;
    private String tipo;
    private String observacoes;
    private ParticipantesConsultaDto cliente;
    private ParticipantesConsultaDto veterinario;
    private ParticipantesConsultaDto atendente;
    private String pet;
    private LocalDateTime dataConsulta;
    private LocalDateTime dataSolicitacao;
    private StatusConsultaEnum status;
    private String motivoIndeferimento;
    private String motivoCancelamento;
    private LocalDateTime dataAtendimento;
    private LocalDateTime dataCancelamento;
    private LocalDateTime dataFinalizado;
    private LocalDateTime dataIniciado;

    public DetalhesConsultaDto(ConsultaModel consulta) {
        this.id = consulta.getId();
        this.tipo = consulta.getTipoConsulta().getNome();
        this.observacoes = consulta.getObservacoes();
        this.cliente = new ParticipantesConsultaDto(consulta.getSolicitante());
        this.atendente = consulta.getAtendente() != null ? new ParticipantesConsultaDto(consulta.getAtendente()) : null;
        this.veterinario = new ParticipantesConsultaDto(consulta.getVeterinario());
        this.pet = consulta.getPet().getNome();
        this.dataConsulta = consulta.getDataConsulta();
        this.dataSolicitacao = consulta.getSolicitadoEm();
        this.status = consulta.getStatus();
        this.motivoIndeferimento = consulta.getMotivoIndeferimento();
        this.motivoCancelamento = consulta.getMotivoCancelamento();
        this.dataAtendimento = consulta.getDeferidoEm();
        this.dataCancelamento = consulta.getCanceladoEm();
        this.dataFinalizado = consulta.getFinalizadoEm();
        this.dataIniciado = consulta.getIniciadoEm();
    }
}
