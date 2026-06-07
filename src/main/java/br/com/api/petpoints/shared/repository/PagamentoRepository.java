package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.PagamentoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagamentoRepository extends JpaRepository<PagamentoModel, Long> {
    List<PagamentoModel> findAllByEmitidoPor_Id(Long idUsuario);
}
