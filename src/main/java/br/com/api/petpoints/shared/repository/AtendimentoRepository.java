package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.enums.StatusAtendimentoEnum;
import br.com.api.petpoints.shared.models.AtendimentoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AtendimentoRepository extends JpaRepository<AtendimentoModel, Long> {

    List<AtendimentoModel> findAllByAtendente_Id(Long id);
    List<AtendimentoModel> findAllByAtendente_IdAndStatus(Long id, StatusAtendimentoEnum status);
    Optional<AtendimentoModel> findByChat_Id(Long id);
}
