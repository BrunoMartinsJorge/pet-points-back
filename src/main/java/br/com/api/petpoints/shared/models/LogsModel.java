package br.com.api.petpoints.shared.models;

import br.com.api.petpoints.shared.enums.TipoLogEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(min = 1, max = 150)
    private String mensagem;

    @Enumerated(EnumType.STRING)
    private TipoLogEnum tipo;

    @ManyToOne
    @Column(name = "lancado_por")
    private UsuarioModel lancadoPor;

    @CreationTimestamp
    @Column(name = "registrado_em")
    private LocalDateTime registradoEm;
}
