package br.com.api.petpoints.shared.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat-participante")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChatParticipanteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private ChatModel chat;

    @ManyToOne
    @Column(name = "participante_id")
    private UsuarioModel participante;
}
