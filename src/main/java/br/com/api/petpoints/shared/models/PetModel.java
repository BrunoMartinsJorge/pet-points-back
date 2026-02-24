package br.com.api.petpoints.shared.models;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.enums.TipoAnimalEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pet")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tutor_id")
    private UsuarioModel tutor;

    private String nome;

    @Enumerated(EnumType.STRING)
    private GeneroEnum genero;

    @CreationTimestamp
    @Column(name = "registrado_em")
    private LocalDateTime registradoEm;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_animal")
    private TipoAnimalEnum tipo;

    private String raca;

    private String observacoes;

    private UUID imagem;
}
