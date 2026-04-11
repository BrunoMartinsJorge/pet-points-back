package br.com.api.petpoints.shared.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensagem")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MensagemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private ChatModel chat;

    @ManyToOne
    @JoinColumn(name = "remetente_id")
    private UsuarioModel remetente;

    @Length(min = 8, max = 255)
    @NotBlank(message = "O campo 'conteudo' não pode estar em branco!")
    private String conteudo;

    @CreationTimestamp
    @Column(name = "enviado_em")
    private LocalDateTime enviadoEm;

    public MensagemModel(ChatModel chat, UsuarioModel remetente, String conteudo) {
        this.chat = chat;
        this.remetente = remetente;
        this.conteudo = conteudo;
    }
}
