package br.com.api.petpoints.modules.cliente.features.meuspets.services;

import br.com.api.petpoints.modules.cliente.features.meuspets.dto.MeuPetConsultaDto;
import br.com.api.petpoints.modules.cliente.features.meuspets.dto.MeuPetDto;
import br.com.api.petpoints.modules.cliente.features.meuspets.dto.PetInformacoesDto;
import br.com.api.petpoints.modules.cliente.features.meuspets.forms.NovoPetForm;
import br.com.api.petpoints.modules.usuario.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.models.ConsultaModel;
import br.com.api.petpoints.shared.models.PetModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import br.com.api.petpoints.shared.repository.PetRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeusPetsClienteServiceImpl implements MeusPetsClienteService {

    private final UsuarioRepository usuarioRepository;
    private final PetRepository petRepository;
    private final ConsultaRepository consultaRepository;

    private UsuarioModel getUsuarioPorId(Long idUsuario) {
        return this.usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioNaoEncontrado("O usuário com ID: " + idUsuario + " não encontrado!"));
    }

    private PetModel getPetPorId(Long idPet) {
        return this.petRepository.findById(idPet).orElseThrow(() -> new ObjectNotFoundException("O pet com : " + idPet + " não encontrado!"));
    }

    @Override
    public List<MeuPetDto> listarMeusPets(Long idUsuario) {
        List<PetModel> meusPets = this.petRepository.findAllByTutor_Id(idUsuario);
        return meusPets.stream().map(MeuPetDto::new).toList();
    }

    @Override
    public MeuPetDto registrarNovoPet(NovoPetForm form, Long idUsuario) {
        Optional<UsuarioModel> user = this.usuarioRepository.findById(idUsuario);
        if (user.isEmpty()) throw new UsuarioNaoEncontrado("Usuário com ID " + idUsuario + " não encontrado!");
        PetModel pet = new PetModel(form, user.get());
        pet = this.petRepository.save(pet);
        return new MeuPetDto(pet);
    }

    @Override
    public String gerarCarteirinha(Long idPet, Model model) {
        MeuPetDto pet = new MeuPetDto(this.petRepository.findById(idPet).orElseThrow(() -> new ObjectNotFoundException("Pet com ID: " + idPet + " não encontrado!")));
        model.addAttribute("pet", pet);
        return "carteirinha";
    }

    @Override
    public List<MeuPetConsultaDto> listarPetsConsultas(Long idUsuario) {
        List<ConsultaModel> consultas = this.consultaRepository.findAllBySolicitante_Id(idUsuario).stream().filter(consulta -> consulta.getStatus() != StatusConsultaEnum.CANCELADO && consulta.getStatus() != StatusConsultaEnum.FINALIZADO).toList();
        return MeuPetConsultaDto.convert(consultas);
    }

    public PetInformacoesDto buscarInformacoesDePet(Long idUsuario, Long idPet) {
        PetModel pet = this.getPetPorId(idPet);
        List<PetModel> petsRelacionados = this.petRepository.buscarPetsRelacionados(idUsuario, pet.getGenero(), pet.getTipo(), pet.getId());
        return new PetInformacoesDto(pet, petsRelacionados);
    }
}
