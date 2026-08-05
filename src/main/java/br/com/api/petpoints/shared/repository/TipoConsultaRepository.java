package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.TipoConsultaModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoConsultaRepository extends JpaRepository<TipoConsultaModel, Long> {
    List<TipoConsultaModel> findAllByVeterinarios(List<UsuarioModel> veterinarios);
}
