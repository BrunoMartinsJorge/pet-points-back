package br.com.api.petpoints.shared.features.chatatendimento.dto;

import br.com.api.petpoints.shared.models.MensagemModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMensagemDto {
    private Long id;
    private Long idChat;
    private Long remetenteId;
    private String enviadoPor;
    private String mensagem;
    private LocalDateTime enviadoEm;
    private boolean enviadoPorVoce;

    public ChatMensagemDto(MensagemModel message, Long idUsuario) {
        this.id = message.getId();
        this.idChat = message.getChat().getId();
        this.remetenteId = message.getRemetente().getId();
        this.enviadoPor = message.getRemetente().getNome();
        this.mensagem = message.getConteudo();
        this.enviadoEm = message.getEnviadoEm();
        this.enviadoPorVoce = Objects.equals(message.getRemetente().getId(), idUsuario);
    }

    public static List<ChatMensagemDto> convert(List<MensagemModel> messages, Long idUsuario) {
        return messages.stream().map(mensagem -> new ChatMensagemDto(mensagem, idUsuario)).toList();
    }
}
