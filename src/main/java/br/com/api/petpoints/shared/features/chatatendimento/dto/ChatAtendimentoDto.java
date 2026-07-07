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
public class ChatAtendimentoDto {

    private Long id;
    private Long chatId;
    private String cliente;
    private StatusAtendimentoEnum status;
    private LocalDateTime solicitadoEm;
    private String mensagem;

    public ChatAtendimentoDto(AtendimentoModel atendimento) {
        this.id = atendimento.getId();
        this.chatId = atendimento.getChat().getId();
        this.cliente = atendimento.getCliente().getNome();
        this.status = atendimento.getStatus();
        this.solicitadoEm = atendimento.getSolicitadoEm();
        this.mensagem = atendimento.getPrimeiraMensagem();
    }

    public static List<ChatAtendimentoDto> convert(List<AtendimentoModel> atendimentos) {
        return atendimentos.stream().map(ChatAtendimentoDto::new).toList();
    }
}
