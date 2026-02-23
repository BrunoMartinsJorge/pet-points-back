package br.com.api.petpoints.shared.features.logs;

import br.com.api.petpoints.modules.usuario.models.UsuarioModel;
import br.com.api.petpoints.shared.enums.TipoLogEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "logs")
public class LogModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @NotNull(message = "O 'usuário' não pode ser nulo!")
    private UsuarioModel feitoPor;

    @CreationTimestamp
    @Column(name = "gerado_em")
    private LocalDateTime geradoEm;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "O 'tipo' não pode ser nulo!")
    @NotBlank(message = "O 'tipo' não pode ser vazio!")
    private TipoLogEnum tipo;

    @NotNull(message = "A 'mensagem' não pode ser nula!")
    @NotBlank(message = "A 'mensagem' não pode ser vazia!")
    private String mensagem;

    public LogModel(UsuarioModel feitoPor, TipoLogEnum tipo, String mensagem){
        this.feitoPor = feitoPor;
        this.tipo = tipo;
        this.mensagem = mensagem;
    }
}
