package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.MovimentacaoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimentacaoRepository extends JpaRepository<MovimentacaoModel, Long> {
    List<MovimentacaoModel> findAllByProduto_IdAndMovimentadoPor_Id(Long idProduto, Long idUsuario);
}
