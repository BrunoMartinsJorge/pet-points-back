package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto;

import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto.AvaliacaoConsultaDto;
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
public class InformacoesConsultaSelecionadaDto {

    private Long id;
    private InformacoesPetConsultaSelecionadaDto pet;
    private InformacoesClienteConsultaSelecionadaDto cliente;
    private AvaliacaoConsultaDto avaliacao;
    private String tipo;
    private String observacoes;
    private LocalDateTime dataSolicitacao;
    private LocalDateTime dataDeferimento;
    private LocalDateTime dataConsulta;
    private LocalDateTime dataFinalizacao;
    private String atendente;
    private StatusConsultaEnum status;

    public InformacoesConsultaSelecionadaDto(ConsultaModel consulta) {
        this.id = consulta.getId();
        this.pet = new InformacoesPetConsultaSelecionadaDto(consulta.getPet());
        this.cliente = new InformacoesClienteConsultaSelecionadaDto(consulta.getSolicitante());
        this.avaliacao = consulta.getAvaliacao() != null ? new AvaliacaoConsultaDto(consulta.getAvaliacao()) : null;
        this.tipo = consulta.getTipoConsulta().getNome();
        this.observacoes = consulta.getObservacoes();
        this.dataConsulta = consulta.getDataConsulta();
        this.dataSolicitacao = consulta.getSolicitadoEm();
        this.dataDeferimento = consulta.getDeferidoEm();
        this.dataFinalizacao = consulta.getFinalizadoEm();
        this.atendente = consulta.getAtendente() != null ? consulta.getAtendente().getNome() : "";
        this.status = consulta.getStatus();
    }
}
