package br.com.api.petpoints.modules.usuario.repository;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.modules.usuario.models.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {
    Optional<UsuarioModel> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<UsuarioModel> findByEmailAndPassword(String email, String password);
    boolean existsByEmailAndPassword(String email, String password);
    List<UsuarioModel> findAllByPermissaoNotContains(TipoUsuario role);
}
