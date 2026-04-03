package br.com.api.petpoints.modules.cliente.features.minhasconsultas.dto;

import br.com.api.petpoints.modules.cliente.features.meuspets.dto.MeuPetDto;
import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.models.*;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MinhasConsultasDto {
    private Long id;
    private String iniciadoEm;
    private String finalizadoEm;
    private String status;
    private String atendente;
    private String veterinario;
    private MeuPetDto pet;
    private String tipoConsulta;
    private String solicitadoEm;
    private String deferidoEm;
    private String motivoIndeferimento;
    private String dataConsulta;
    private String resumoConsulta;
    private String canceladoEm;
    private String motivoCancelamento;
    // private PagamentoModel pagamento;
    // private AvaliacaoModel avaliacao;

    public MinhasConsultasDto(ConsultaModel consulta) {
        this.id = consulta.getId();
        this.iniciadoEm = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getIniciadoEm());
        this.finalizadoEm = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getFinalizadoEm());
        this.status = consulta.getStatus().getDescricao();
        this.atendente = consulta.getAtendente() != null ? consulta.getAtendente().getNome() : null;
        this.veterinario = consulta.getVeterinario().getNome();
        this.pet = new MeuPetDto(consulta.getPet());
        this.tipoConsulta = consulta.getTipoConsulta().getDescricao();
        this.solicitadoEm = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getSolicitadoEm());
        this.deferidoEm = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getDeferidoEm());
        this.motivoIndeferimento = consulta.getMotivoIndeferimento();
        this.dataConsulta = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getDataConsulta());
        this.resumoConsulta = consulta.getResumoConsulta();
        this.canceladoEm = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getCanceladoEm());
        this.motivoCancelamento = consulta.getMotivoCancelamento();
    }
}
