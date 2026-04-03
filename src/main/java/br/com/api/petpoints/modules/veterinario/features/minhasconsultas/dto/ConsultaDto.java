package br.com.api.petpoints.modules.veterinario.features.minhasconsultas.dto;

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
public class ConsultaDto {

    private Long id;
    private String iniciadoEm;
    private String finalizadoEm;
    private StatusConsultaEnum status;
    private UsuarioConsultaDto solicitante;
    private UsuarioConsultaDto atendente;
    private PetConsultaDto pet;
    private String tipoConsulta;
    private String solicitadoEm;
    private String deferidoEm;
    private String motivoIndeferimento;
    private String dataConsulta;
    private String resumoConsulta;
    private String canceladoEm;
    private String motivoCancelamento;
    private String observacoes;

    public ConsultaDto(ConsultaModel consulta) {
        id = consulta.getId();
        iniciadoEm = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getIniciadoEm());
        finalizadoEm = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getFinalizadoEm());
        status = consulta.getStatus();
        solicitante = new UsuarioConsultaDto(consulta.getSolicitante());
        atendente = new UsuarioConsultaDto();
        pet = new PetConsultaDto();
        tipoConsulta = consulta.getTipoConsulta().getNome();
        solicitadoEm = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getSolicitadoEm());
        deferidoEm = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getDeferidoEm());
        motivoIndeferimento = consulta.getMotivoIndeferimento();
        dataConsulta = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getDataConsulta());
        resumoConsulta = consulta.getResumoConsulta();
        canceladoEm = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getCanceladoEm());
        motivoCancelamento = consulta.getMotivoCancelamento();
        observacoes = consulta.getObservacoes();
    }

    public static List<ConsultaDto> convert(List<ConsultaModel> consultas) {
        return consultas.stream().map(ConsultaDto::new).toList();
    }
}
