package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.MensagemModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensagemRepository extends JpaRepository<MensagemModel, Long> {
    List<MensagemModel> findByChat_IdOrderByEnviadoEmAsc(Long idChat);
}
