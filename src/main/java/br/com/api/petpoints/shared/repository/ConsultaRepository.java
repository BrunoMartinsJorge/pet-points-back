package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.models.ConsultaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<ConsultaModel, Long> {
    List<ConsultaModel> findAllBySolicitante_Id(Long id);
    List<ConsultaModel> findAllBySolicitante_IdAndPagamentoIsNull(Long id);
    List<ConsultaModel> findAllBySolicitante_IdAndStatus(Long id, StatusConsultaEnum status);
    List<ConsultaModel> findAllByVeterinario_Id(Long id);
    List<ConsultaModel> findAllByStatus(StatusConsultaEnum status);

    @Query("SELECT u FROM ConsultaModel u WHERE u.solicitante.id = ?1 and (u.status = 'Aprovada' or u.status = 'Pendente' or u.status = 'Iniciado')")
    List<ConsultaModel> buscarConsultasPendentesOuConfirmadasPorUsuario(Long idUsuario);

    @Query("SELECT u FROM ConsultaModel u WHERE u.solicitante.id = ?1 and u.pagamento is null and u.status = 'Finalizado'")
    List<ConsultaModel> buscarConsultasPorUsuarioComPagamentosPendentes(Long idUsuario);
}
