package br.com.api.petpoints.domain.users.cliente.features.meuspets.services;

import br.com.api.petpoints.domain.users.cliente.features.meuspets.dto.MeuPetConsultaDto;
import br.com.api.petpoints.domain.users.cliente.features.meuspets.dto.MeuPetDto;
import br.com.api.petpoints.domain.users.cliente.features.meuspets.dto.PetPodeSerDeletadoDto;
import br.com.api.petpoints.domain.users.cliente.features.meuspets.forms.EditarPetForm;
import br.com.api.petpoints.domain.users.cliente.features.meuspets.forms.NovoPetForm;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto.MinhasConsultasDto;
import br.com.api.petpoints.shared.models.ArquivosModel;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MeusPetsClienteService {
    List<MeuPetDto> listarMeusPets(Long idUsuario);
    MeuPetDto registrarNovoPet(NovoPetForm form, MultipartFile foto, Long idUsuario);
    String gerarCarteirinha(Long idPet, Model model);
    byte[] baixarCarteirinha(Long idPet);
    List<MeuPetConsultaDto> listarPetsConsultas(Long idUsuario);
    List<MinhasConsultasDto> listarConsultasPet(Long idPet);
    ArquivosModel buscarImagemPet(Long id);
    void editarPet(Long idPet, Long idCliente, EditarPetForm form, MultipartFile arquivo);
    PetPodeSerDeletadoDto verificarPetPodeSerDeletado(Long idPet, Long idCliente);
    void deletarPet(Long idPet, Long idCliente);
}
