package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.enums.StatusAtendimentoEnum;
import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.models.AtendimentoModel;
import br.com.api.petpoints.shared.models.ConsultaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<ConsultaModel, Long> {
    List<ConsultaModel> findAllBySolicitante_Id(Long id);
    List<ConsultaModel> findAllByPet_IdAndStatus(Long id, StatusConsultaEnum status);
    List<ConsultaModel> findAllBySolicitante_IdAndPagamentoIsNull(Long id);
    List<ConsultaModel> findAllBySolicitante_IdAndStatus(Long id, StatusConsultaEnum status);
    List<ConsultaModel> findAllByVeterinario_Id(Long id);
    List<ConsultaModel> findAllByAtendente_Id(Long id);
    List<ConsultaModel> findAllByStatus(StatusConsultaEnum status);
    List<ConsultaModel> findAllByPet_Id(Long id);
    List<ConsultaModel> findAllByPet_IdOrderByDataConsultaDesc(Long id);
    List<ConsultaModel> findAllByPagamentoIsNull();
    boolean existsByPet_IdAndStatus(Long id, StatusConsultaEnum status);

    @Query("SELECT u FROM ConsultaModel u WHERE u.solicitante.id = ?1 and (u.status = 'APROVADA' or u.status = 'INICIADA')")
    List<ConsultaModel> buscarConsultasConfirmadasPorUsuario(Long idUsuario);

    @Query("SELECT u FROM ConsultaModel u WHERE u.solicitante.id = ?1 and u.pagamento is null and u.status = 'Finalizado'")
    List<ConsultaModel> buscarConsultasPorUsuarioComPagamentosPendentes(Long idUsuario);

    @Query("SELECT h FROM ConsultaModel h WHERE h.veterinario.id = ?1 AND h.status = ?2 AND h.avaliacao IS NOT NULL")
    List<ConsultaModel> buscarConsultaPorStatusIdVeterinario(Long idVeterinario, StatusConsultaEnum status);

    @Query("SELECT u FROM ConsultaModel u WHERE u.pet.id = ?1 and u.status = 'PENDENTE' or u.status = 'INICIADO'")
    List<ConsultaModel> buscarConsultasPendenteOuIniciadas(Long idPet);
}
