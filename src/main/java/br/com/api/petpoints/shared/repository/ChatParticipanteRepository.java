package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.ChatModel;
import br.com.api.petpoints.shared.models.ChatParticipanteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipanteRepository extends JpaRepository<ChatParticipanteModel, Long> {

    @Query("""
                SELECT cp.chat FROM ChatParticipanteModel cp
                WHERE cp.chat.id IN (
                    SELECT cp2.chat.id FROM ChatParticipanteModel cp2
                    WHERE cp2.participante.id IN (:user1, :user2)
                    GROUP BY cp2.chat.id
                    HAVING COUNT(DISTINCT cp2.participante.id) = 2
                )
            """)
    List<ChatModel> buscarChatEntreUsuarios(
            @Param("user1") Long user1,
            @Param("user2") Long user2
    );
}
