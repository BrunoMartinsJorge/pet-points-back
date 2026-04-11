package br.com.api.petpoints.shared.features.chatinterno.dto;

import br.com.api.petpoints.shared.models.MensagemModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MensagemInternaDto {
    private Long id;
    private Long idChat;
    private ParticipanteChatInternoDto remetente;
    private ParticipanteChatInternoDto destinatario;
    private String mensagem;
    private LocalDateTime enviadoEm;
    private boolean enviadoPorVoce;

    public MensagemInternaDto(MensagemModel message, UsuarioModel remetente, UsuarioModel destinatario) {
        this.id = message.getId();
        this.idChat = message.getChat().getId();
        this.remetente = new ParticipanteChatInternoDto(remetente);
        this.destinatario = new ParticipanteChatInternoDto(destinatario);
        this.mensagem = message.getConteudo();
        this.enviadoEm = message.getEnviadoEm();
        this.enviadoPorVoce = Objects.equals(message.getRemetente().getId(), remetente.getId());
    }

    public static List<MensagemInternaDto> convert(Map<UsuarioModel, MensagemModel> messages) {
        List<MensagemModel> mensagens = messages.values().stream().toList();
        UsuarioModel destinatario = messages.keySet().stream().findFirst().get();
        return mensagens.stream().map(mensagem -> new MensagemInternaDto(mensagem, mensagem.getRemetente(), destinatario)).toList();
    }
}
