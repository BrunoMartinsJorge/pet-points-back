package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.enums.StatusAtendimentoEnum;
import br.com.api.petpoints.shared.models.AtendimentoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AtendimentoRepository extends JpaRepository<AtendimentoModel, Long> {

    List<AtendimentoModel> findAllByAtendente_Id(Long id);
    List<AtendimentoModel> findAllByAtendente_IdAndAvaliacaoIsNotNull(Long id);

    List<AtendimentoModel> findAllByCliente_Id(Long id);

    List<AtendimentoModel> findAllByAtendente_IdAndStatus(Long id, StatusAtendimentoEnum status);

    List<AtendimentoModel> findAllByCliente_IdAndStatus(Long id, StatusAtendimentoEnum status);

    Optional<AtendimentoModel> findByChat_Id(Long id);

    @Query("SELECT h FROM AtendimentoModel h WHERE h.atendente.id = ?1 AND h.status = ?2 AND h.avaliacao IS NOT NULL")
    List<AtendimentoModel> buscarAvaliacoesAtendimento(Long idAtendente, StatusAtendimentoEnum status);

    @Query("""
                SELECT h
                FROM AtendimentoModel h
                WHERE h.status = :status
                  AND h.avaliacao IS NOT NULL
            """)
    List<AtendimentoModel> buscarAvaliacoesFinalizadas(StatusAtendimentoEnum status);
}
