package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.MensagemModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MensagemRepository extends JpaRepository<MensagemModel, Long> {
}
