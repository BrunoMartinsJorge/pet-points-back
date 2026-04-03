package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.enums.TipoAnimalEnum;
import br.com.api.petpoints.shared.models.PetModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<PetModel, Long> {
    List<PetModel> findAllByTutor_Id(Long id);

    @Query("SELECT u FROM PetModel u WHERE u.tutor.id = ?1 and (u.genero = ?2 or u.tipo = ?3) and u.id != ?4")
    List<PetModel> buscarPetsRelacionados(Long idCliente, GeneroEnum genero, TipoAnimalEnum tipo, Long idPet);
}
