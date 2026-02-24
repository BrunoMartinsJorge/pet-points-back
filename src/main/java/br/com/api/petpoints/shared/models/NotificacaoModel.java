package br.com.api.petpoints.shared.models;

import br.com.api.petpoints.shared.enums.TiposNotificacoesEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UsuarioModel para;

    @Length(min = 10, max = 250)
    private String conteudo;

    @CreationTimestamp
    private LocalDateTime enviadoEm;

    private boolean visto = false;

    @Enumerated(EnumType.STRING)
    private TiposNotificacoesEnum tipo;
}
