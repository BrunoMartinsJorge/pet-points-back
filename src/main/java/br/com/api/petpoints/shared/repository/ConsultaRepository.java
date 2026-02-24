package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.ConsultaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultaRepository extends JpaRepository<ConsultaModel, Long> {
}
