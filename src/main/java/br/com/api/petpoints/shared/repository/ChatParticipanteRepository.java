package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.ChatParticipanteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatParticipanteRepository extends JpaRepository<ChatParticipanteModel, Long> {
}
