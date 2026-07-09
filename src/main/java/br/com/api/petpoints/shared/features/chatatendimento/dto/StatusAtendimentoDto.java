package br.com.api.petpoints.shared.features.chatatendimento.dto;

import br.com.api.petpoints.shared.enums.StatusAtendimentoEnum;
import br.com.api.petpoints.shared.models.AtendimentoModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatusAtendimentoDto {

    private Long idAtendimento;
    private Long idChat;
    private StatusAtendimentoEnum status;
    private String cliente;
    private String atendente;

    public StatusAtendimentoDto(AtendimentoModel atendimento) {
        this.idAtendimento = atendimento.getId();
        this.idChat = atendimento.getChat().getId();
        this.status = atendimento.getStatus();
        this.cliente = atendimento.getCliente() != null ? atendimento.getCliente().getNome() : null;
        this.atendente = atendimento.getAtendente() != null ? atendimento.getAtendente().getNome() : null;
    }
}