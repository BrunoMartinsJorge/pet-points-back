package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.ChatModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<ChatModel, Long> {
}
