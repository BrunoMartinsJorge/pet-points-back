package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.ComprovanteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComprovanteRepository extends JpaRepository<ComprovanteModel, Long> {
}
