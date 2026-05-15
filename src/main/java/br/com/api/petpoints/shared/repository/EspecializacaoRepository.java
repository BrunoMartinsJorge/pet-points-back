package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.EspecializacaoModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EspecializacaoRepository extends JpaRepository<EspecializacaoModel, Long> {
    @Query("SELECT e FROM EspecializacaoModel e WHERE :veterinario MEMBER OF e.veterinarios")
    List<EspecializacaoModel> buscarPorVeterinario(UsuarioModel veterinario);
}
