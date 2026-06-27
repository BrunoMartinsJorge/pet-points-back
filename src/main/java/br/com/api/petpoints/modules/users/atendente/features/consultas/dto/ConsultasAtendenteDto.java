package br.com.api.petpoints.modules.users.atendente.features.consultas.dto;

import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.models.*;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsultasAtendenteDto {

    private Long id;
    private String iniciadoEm;
    private String finalizadoEm;
    private StatusConsultaEnum status;
    private String solicitante;
    private String atendente;
    private String veterinario;
    private String pet;
    private String tipoConsulta;
    private String solicitadoEm;
    private String deferidoEm;
    private String motivoIndeferimento;
    private String dataConsulta;
    private String resumoConsulta;
    private String canceladoEm;
    private String motivoCancelamento;
    private String observacoes;

    public ConsultasAtendenteDto(ConsultaModel consulta) {
        this.id = consulta.getId();
        this.iniciadoEm = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getIniciadoEm());
        this.finalizadoEm = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getFinalizadoEm());
        this.status = consulta.getStatus();
        this.solicitante = consulta.getSolicitante().getNome();
        this.atendente = consulta.getAtendente() != null ? consulta.getAtendente().getNome() : "Consulta Não Deferida!";
        this.veterinario = consulta.getVeterinario().getNome();
        this.pet = consulta.getPet().getNome();
        this.tipoConsulta = consulta.getTipoConsulta().getDescricao();
        this.solicitadoEm = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getSolicitadoEm());
        this.deferidoEm = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getDeferidoEm());
        this.motivoIndeferimento = consulta.getMotivoIndeferimento();
        this.dataConsulta = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getDataConsulta());
        this.resumoConsulta = consulta.getResumoConsulta();
        this.canceladoEm = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getCanceladoEm());
        this.motivoCancelamento = consulta.getMotivoCancelamento();
        this.observacoes = consulta.getObservacoes();
    }
}
