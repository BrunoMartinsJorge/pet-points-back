package br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto;

import br.com.api.petpoints.shared.models.ConsultaModel;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetalhesConsultaSelecionadaDto {

    private Long id;
    private String atendente;
    private String dataDeferimento;
    private String dataCancelamento;
    private String motivoIndeferimento;
    private String motivoCancelamento;
    private String pet;
    private String observacoes;
    private String iniciadoEm;
    private String finalizadoEm;

    public DetalhesConsultaSelecionadaDto(ConsultaModel consulta) {
        this.id = consulta.getId();
        this.atendente = consulta.getAtendente() == null ? "Não Atendido" : consulta.getAtendente().getNome();
        this.dataDeferimento = consulta.getDeferidoEm() == null ? "Não Aprovada" : LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getDeferidoEm());
        this.dataCancelamento = consulta.getCanceladoEm() == null ? "Não Cancelado" : LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getCanceladoEm());
        this.motivoIndeferimento = consulta.getMotivoIndeferimento();
        this.motivoCancelamento = consulta.getMotivoCancelamento();
        this.pet = consulta.getPet().getNome();
        this.observacoes = consulta.getObservacoes();
        this.iniciadoEm = consulta.getIniciadoEm() == null ? "Não Iniciado" : LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getIniciadoEm());
        this.finalizadoEm = consulta.getFinalizadoEm() == null ? "Não Finalizado" : LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getFinalizadoEm());
    }
}
