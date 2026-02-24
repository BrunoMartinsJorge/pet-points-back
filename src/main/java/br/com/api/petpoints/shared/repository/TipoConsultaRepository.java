package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.TipoConsultaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoConsultaRepository extends JpaRepository<TipoConsultaModel, Long> {
}
