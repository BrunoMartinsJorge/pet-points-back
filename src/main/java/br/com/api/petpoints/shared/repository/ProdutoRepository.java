package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.ProdutoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<ProdutoModel, Long> {

    @Query("SELECT u FROM ProdutoModel u WHERE u.quantidadeEstoque < u.quantidadeMinima")
    List<ProdutoModel> listarProdutosAbaixoDeEstoque();
}
