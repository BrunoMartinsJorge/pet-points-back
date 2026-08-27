package br.com.api.petpoints.domain.users.gerente.features.consultas.dto;

import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
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
    private String pet;
    private LocalDateTime dataConsulta;
    private double valor;
    private TipoPagamentoEnum formaPagamento;

    public ConsultaClinicaDto(ConsultaModel consulta) {
        this.id = consulta.getId();
        this.tipo = new TipoConsultaAtributoDto(consulta.getTipoConsulta());
        this.status = consulta.getStatus();
        this.solicitadoEm = consulta.getSolicitadoEm();
        this.observacoes = consulta.getObservacoes();
        this.cliente = new ParticipantesConsultaDto(consulta.getSolicitante());
        this.veterinario = new ParticipantesConsultaDto(consulta.getVeterinario());
        this.pet = consulta.getPet() != null ? consulta.getPet().getNome() : null;
        this.dataConsulta = consulta.getDataConsulta();
        this.valor = consulta.getTipoConsulta() != null ? consulta.getTipoConsulta().getValor() : 0.0;
        this.formaPagamento = consulta.getFormaPagamento();
    }

    public static List<ConsultaClinicaDto> convert(List<ConsultaModel> consultas) {
        return consultas.stream().map(ConsultaClinicaDto::new).toList();
    }
}
