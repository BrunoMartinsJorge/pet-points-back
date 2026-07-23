package br.com.api.petpoints.domain.users.gerente.features.pets.service;

import br.com.api.petpoints.domain.users.gerente.features.pets.dto.*;
import br.com.api.petpoints.domain.users.gerente.features.pets.form.RelatorioPetsClinicaForm;

import java.util.List;

public interface PetsClinicaService {
    List<PetsDto> buscarPetsClinica();
    List<TutorDto> buscarTutorsClinica();
    DetalhesPetDto buscarDetalhesPetsClinica(Long idPet);
    DetalhesTutorPetDto buscarDetalhesTutorsClinica(Long idPet);
    List<HistoricoConsultasPetDto> buscarHistoricoConsultasPet(Long idPet);
    byte[] gerarRelatorio(RelatorioPetsClinicaForm form);
}
