package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.MovimentacaoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimentacaoRepository extends JpaRepository<MovimentacaoModel, Long> {
}
