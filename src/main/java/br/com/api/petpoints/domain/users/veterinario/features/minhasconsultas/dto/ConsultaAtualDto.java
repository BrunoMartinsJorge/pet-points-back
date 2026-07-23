package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto;

import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.models.ConsultaModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConsultaAtualDto {

    private Long id;
    private StatusConsultaEnum status;
    private InformacoesPetVeterinarioDto pet;
    private LocalDateTime data;
    private UUID imagemCliente;
    private String tipo;
    private String observacoes;
    private LocalDateTime iniciadoEm;
    private LocalDateTime finalizadoEm;

    public ConsultaAtualDto(ConsultaModel consulta) {
        this.id = consulta.getId();
        this.status = consulta.getStatus();
        this.pet = new InformacoesPetVeterinarioDto(consulta.getPet());
        this.imagemCliente = consulta.getSolicitante().getImagem();
        this.tipo = consulta.getTipoConsulta().getNome();
        this.observacoes = consulta.getObservacoes();
        this.iniciadoEm = consulta.getIniciadoEm();
        this.finalizadoEm = consulta.getFinalizadoEm();
    }
}
