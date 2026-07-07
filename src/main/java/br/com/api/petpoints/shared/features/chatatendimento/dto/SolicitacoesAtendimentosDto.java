package br.com.api.petpoints.shared.features.chatatendimento.dto;

import br.com.api.petpoints.shared.models.AtendimentoModel;
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
public class SolicitacoesAtendimentosDto {

    private Long id;
    private String mensagem;
    private LocalDateTime solicitadoEm;
    private String solicitante;

    public SolicitacoesAtendimentosDto(AtendimentoModel atendimento) {
        this.id = atendimento.getId();
        this.mensagem = atendimento.getPrimeiraMensagem();
        this.solicitadoEm = atendimento.getSolicitadoEm();
        this.solicitante = atendimento.getCliente().getNome();
    }

    public static List<SolicitacoesAtendimentosDto> convert(List<AtendimentoModel> atendimentos) {
        return atendimentos.stream().map(SolicitacoesAtendimentosDto::new).toList();
    }
}
