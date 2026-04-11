package br.com.api.petpoints.modules.auth.repository;

import br.com.api.petpoints.modules.auth.model.TokenRecuperarSenhaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRecuperarSenhaRepository extends JpaRepository<TokenRecuperarSenhaModel, Long> {
    Optional<TokenRecuperarSenhaModel> findByUsuarioModel_IdAndToken(Long id, String token);
}
