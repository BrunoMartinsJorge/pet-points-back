package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.ArquivosModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ArquivoRepository extends JpaRepository<ArquivosModel, UUID> {
}
