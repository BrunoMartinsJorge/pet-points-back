package br.com.api.petpoints.shared.features.chatatendimento.dto;

import br.com.api.petpoints.shared.enums.StatusAtendimentoEnum;
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
public class AtendimentoDto {

    private Long id;
    private Long chatId;
    private String mensagem;
    private LocalDateTime solicitadoEm;
    private String atendente;
    private StatusAtendimentoEnum status;

    public AtendimentoDto(AtendimentoModel atendimento) {
        this.id = atendimento.getId();
        this.chatId = atendimento.getChat() != null ? atendimento.getChat().getId() : null;
        this.mensagem = atendimento.getPrimeiraMensagem();
        this.solicitadoEm = atendimento.getSolicitadoEm();
        this.atendente = atendimento.getAtendente() != null ? atendimento.getAtendente().getNome() : "Não Atendido";
        this.status = atendimento.getStatus();
    }

    public static List<AtendimentoDto> convert(List<AtendimentoModel> atendimentos) {
        return atendimentos.stream().map(AtendimentoDto::new).toList();
    }
}
