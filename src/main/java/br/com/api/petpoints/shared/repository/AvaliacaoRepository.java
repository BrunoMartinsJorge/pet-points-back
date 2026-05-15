package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.AvaliacaoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoRepository extends JpaRepository<AvaliacaoModel, Long> {
}
