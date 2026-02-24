package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.NotificacaoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacaoRepository extends JpaRepository<NotificacaoModel, Long> {
}
