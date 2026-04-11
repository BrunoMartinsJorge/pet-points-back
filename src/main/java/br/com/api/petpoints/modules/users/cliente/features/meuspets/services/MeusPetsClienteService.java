package br.com.api.petpoints.modules.users.cliente.features.meuspets.services;

import br.com.api.petpoints.modules.users.cliente.features.meuspets.dto.MeuPetDto;
import br.com.api.petpoints.modules.users.cliente.features.meuspets.forms.NovoPetForm;
import org.springframework.ui.Model;

import java.util.List;

public interface MeusPetsClienteService {
    List<MeuPetDto> listarMeusPets(Long idUsuario);
    MeuPetDto registrarNovoPet(NovoPetForm form, Long idUsuario);
    String gerarCarteirinha(Long idPet, Model model);
    List<?> listarPetsConsultas(Long idUsuario);

}
