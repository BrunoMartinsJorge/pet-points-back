package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.ParametrosAdicionaisProdutoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParametrosAdicionaisProdutoRepository extends JpaRepository<ParametrosAdicionaisProdutoModel, Long> {
}
