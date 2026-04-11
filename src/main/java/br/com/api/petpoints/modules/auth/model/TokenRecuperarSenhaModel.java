package br.com.api.petpoints.modules.auth.model;

import br.com.api.petpoints.shared.models.UsuarioModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenRecuperarSenhaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private UsuarioModel usuarioModel;
    private String token;
    private LocalDateTime expires_at;
    private Boolean used = false;
}
