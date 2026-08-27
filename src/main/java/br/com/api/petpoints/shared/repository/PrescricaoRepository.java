package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.PrescricaoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrescricaoRepository extends JpaRepository<PrescricaoModel, Long> {
}
