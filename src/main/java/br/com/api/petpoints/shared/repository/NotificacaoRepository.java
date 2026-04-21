package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.NotificacaoModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<NotificacaoModel, Long> {
    List<NotificacaoModel> findByPara_Id(Long idUsuario);
}
