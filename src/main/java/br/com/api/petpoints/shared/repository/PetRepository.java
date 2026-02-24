package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.PetModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JpaRepository<PetModel, Long> {
}
