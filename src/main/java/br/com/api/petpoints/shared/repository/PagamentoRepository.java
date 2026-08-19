package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.PagamentoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<PagamentoModel, Long> {
    List<PagamentoModel> findAllByEmitidoPor_Id(Long idUsuario);
    Optional<PagamentoModel> findByIdPagamentoExterno(String idPagamentoExterno);

    @Query("SELECT h FROM PagamentoModel h where h.statusPagamento = 'PENDENTE'")
    List<PagamentoModel> buscarPagamentosPendentesAtrasados(LocalDateTime dataAtual);
}
