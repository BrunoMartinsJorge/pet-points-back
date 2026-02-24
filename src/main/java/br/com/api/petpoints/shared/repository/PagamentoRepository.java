package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.PagamentoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagamentoRepository extends JpaRepository<PagamentoModel, Long> {
}
