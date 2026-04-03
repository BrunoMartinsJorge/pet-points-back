package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.shared.models.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {
    Optional<UsuarioModel> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailOrCpf(String email, String cpf);
    Optional<UsuarioModel> findByEmailAndPassword(String email, String password);
    boolean existsByEmailAndSenha(String email, String password);
    List<UsuarioModel> findAllByPermissaoNot(TipoUsuario role);
}
