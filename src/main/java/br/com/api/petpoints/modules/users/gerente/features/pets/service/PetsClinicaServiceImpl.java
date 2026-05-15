package br.com.api.petpoints.modules.users.gerente.features.pets.service;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.modules.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.modules.users.gerente.features.pets.dto.*;
import br.com.api.petpoints.modules.users.gerente.features.pets.form.RelatorioPetsClinicaForm;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.models.ConsultaModel;
import br.com.api.petpoints.shared.models.MovimentacaoModel;
import br.com.api.petpoints.shared.models.PetModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import br.com.api.petpoints.shared.repository.PetRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import br.com.api.petpoints.shared.utils.ColunaRelatorio;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import br.com.api.petpoints.shared.utils.RelatoriosUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PetsClinicaServiceImpl implements PetsClinicaService {

    private final PetRepository petRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConsultaRepository consultaRepository;
    private final RelatoriosUtils relatoriosUtils;

    @Override
    public List<PetsDto> buscarPetsClinica() {
        return PetsDto.convert(this.petRepository.findAll());
    }

    @Override
    public List<TutorDto> buscarTutorsClinica() {
        return TutorDto.convert(this.usuarioRepository.findAllByPermissao(TipoUsuario.C));
    }

    @Override
    public DetalhesPetDto buscarDetalhesPetsClinica(Long idPet) {
        PetModel pet = this.petRepository.findById(idPet).orElseThrow(() -> new ObjectNotFoundException("O pet com o ID: " + idPet + " não foi encontrado!"));
        return new DetalhesPetDto(pet);
    }

    @Override
    public DetalhesTutorPetDto buscarDetalhesTutorsClinica(Long idPet) {
        UsuarioModel usuario = this.petRepository.findById(idPet).orElseThrow(() -> new ObjectNotFoundException("O pet com o ID: " + idPet + " não foi encontrado!")).getTutor();
        return new DetalhesTutorPetDto(usuario);
    }

    @Override
    public List<HistoricoConsultasPetDto> buscarHistoricoConsultasPet(Long idPet) {
        List<ConsultaModel> consultas = this.consultaRepository.findAllByPet_Id(idPet);
        return HistoricoConsultasPetDto.convert(consultas);
    }

    @Override
    public byte[] gerarRelatorio(RelatorioPetsClinicaForm form) {
        List<PetModel> pets = this.filtrarPets(form);
        String titulo = "Relatório de Pets da Clínica";
        List<ColunaRelatorio> colunas = List.of(
                new ColunaRelatorio("ID", m -> ((PetModel) m).getId()),
                new ColunaRelatorio("Nome", m -> ((PetModel) m).getNome()),
                new ColunaRelatorio("Gênero", m -> ((PetModel) m).getGenero().getDescricao()),
                new ColunaRelatorio("Tipo", m -> ((PetModel) m).getTipo().getDescricao()),
                new ColunaRelatorio("Tutor", m -> ((PetModel) m).getTutor().getNome()),
                new ColunaRelatorio("Registrado Em", m -> (LocalDateTimeUtils.converterLocalDateTimeParaPtBr(((PetModel) m).getRegistradoEm())))
        );
        return this.relatoriosUtils.gerarRelatorioGenerico(colunas, pets, titulo, "");
    }

    private List<PetModel> filtrarPets(RelatorioPetsClinicaForm form) {
        List<PetModel> pets = this.petRepository.findAll();
        if (!Objects.equals(form.getTipo(), ""))
            pets = pets.stream().filter(pet -> pet.getTipo().toString().equals(form.getTipo())).toList();
        if (!Objects.equals(form.getNome(), ""))
            pets = pets.stream().filter(pet -> pet.getNome().contains(form.getNome())).toList();
        if (!Objects.equals(form.getGenero(), ""))
            pets = pets.stream().filter(pet -> pet.getGenero().toString().equals(form.getGenero())).toList();
        if (form.getIdTutor() != null)
            pets = pets.stream().filter(pet -> Objects.equals(pet.getTutor().getId(), form.getIdTutor())).toList();
        return pets;
    }
}
