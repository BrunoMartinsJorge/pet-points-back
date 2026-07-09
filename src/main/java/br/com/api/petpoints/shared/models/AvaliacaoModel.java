package br.com.api.petpoints.shared.models;

import br.com.api.petpoints.shared.form.AvaliacaoForm;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Entity
@Table(name = "avaliacao")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvaliacaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(0)
    @Max(5)
    @NotNull(message = "O campo 'pontuacao' não pode ser nulo!")
    private int pontuacao;

    @ManyToOne
    @JoinColumn(name = "avaliado_por")
    private UsuarioModel avaliadoPor;

    @Length(min = 10, max = 150)
    private String observacoes;

    @CreationTimestamp
    @Column(name = "avaliado_em")
    private LocalDateTime avaliadoEm;

    public AvaliacaoModel(AvaliacaoForm form, UsuarioModel cliente) {
        this.pontuacao = form.getPontuacao();
        this.avaliadoPor = cliente;
        this.observacoes = form.getObservacoes();
    }
}
