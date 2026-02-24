package br.com.api.petpoints.shared.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "especializacao")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EspecializacaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany
    @JoinColumn(name = "veterinario_id")
    private Set<UsuarioModel> veterinarios;

    private String descricao;
}
