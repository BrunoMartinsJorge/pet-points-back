package br.com.api.petpoints.shared.dto;

import br.com.api.petpoints.shared.models.AtendimentoModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SolicitacaoRemovidaDto {

    private Long id;
    private String atendente;

    public SolicitacaoRemovidaDto(AtendimentoModel atendimento) {
        this.id = atendimento.getId();
        this.atendente = atendimento.getAtendente() != null ? atendimento.getAtendente().getNome() : null;
    }
}
