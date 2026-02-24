package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.AtendimentoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtendimentoRepository extends JpaRepository<AtendimentoModel, Long> {
}
