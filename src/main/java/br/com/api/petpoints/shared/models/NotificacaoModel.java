package br.com.api.petpoints.shared.models;

import br.com.api.petpoints.shared.enums.TiposNotificacoesEnum;
import br.com.api.petpoints.shared.features.notificacoes.form.NovaNotificacaoForm;
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
    @Column(name = "enviado_em")
    private LocalDateTime enviadoEm;

    private boolean visto = false;

    private String titulo;

    @Enumerated(EnumType.STRING)
    private TiposNotificacoesEnum tipo;

    public NotificacaoModel(NovaNotificacaoForm form, UsuarioModel destinatario) {
        this.para = destinatario;
        this.titulo = form.getTitulo();
        this.conteudo = form.getMensagem();
        this.tipo = form.getTipo();
    }
}
