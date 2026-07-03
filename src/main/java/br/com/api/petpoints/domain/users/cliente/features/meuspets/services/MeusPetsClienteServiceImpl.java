package br.com.api.petpoints.domain.users.cliente.features.meuspets.services;

import br.com.api.petpoints.domain.users.cliente.features.meuspets.dto.MeuPetConsultaDto;
import br.com.api.petpoints.domain.users.cliente.features.meuspets.dto.MeuPetDto;
import br.com.api.petpoints.domain.users.cliente.features.meuspets.dto.PetInformacoesDto;
import br.com.api.petpoints.domain.users.cliente.features.meuspets.dto.PetPodeSerDeletadoDto;
import br.com.api.petpoints.domain.users.cliente.features.meuspets.forms.EditarPetForm;
import br.com.api.petpoints.domain.users.cliente.features.meuspets.forms.NovoPetForm;
import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto.MinhasConsultasDto;
import br.com.api.petpoints.shared.dto.CarteirinhaDto;
import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.enums.StatusPerfilEnum;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.models.ArquivosModel;
import br.com.api.petpoints.shared.models.ConsultaModel;
import br.com.api.petpoints.shared.models.PetModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.ArquivoRepository;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import br.com.api.petpoints.shared.repository.PetRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import br.com.api.petpoints.shared.utils.RelatoriosUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MeusPetsClienteServiceImpl implements MeusPetsClienteService {

    private final UsuarioRepository usuarioRepository;
    private final PetRepository petRepository;
    private final ConsultaRepository consultaRepository;
    private final ArquivoRepository arquivoRepository;
    private final SpringTemplateEngine templateEngine;

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
    public MeuPetDto registrarNovoPet(NovoPetForm form, MultipartFile foto, Long idUsuario) {
        Optional<UsuarioModel> user = this.usuarioRepository.findById(idUsuario);
        if (user.isEmpty()) throw new UsuarioNaoEncontrado("Usuário com ID " + idUsuario + " não encontrado!");
        PetModel pet = new PetModel(form, user.get());
        if (foto != null && !foto.isEmpty())
            pet.setImagem(this.salvarArquivo(foto));
        pet = this.petRepository.save(pet);
        return new MeuPetDto(pet);
    }

    private UUID salvarArquivo(MultipartFile form) {
        if (form.getSize() > 5_000_000) throw new RuntimeException("Arquivo passa de 5MB!");
        List<String> tiposPermitidos = List.of(
                "image/png",
                "image/jpeg",
                "application/pdf"
        );
        if (!tiposPermitidos.contains(form.getContentType()))
            throw new RuntimeException("Tipo inválido");
        ArquivosModel arquivo = new ArquivosModel();
        try {
            arquivo.setConteudo(form.getBytes());
            arquivo.setNome(form.getOriginalFilename());
            arquivo.setTipo(form.getContentType());
            return this.arquivoRepository.save(arquivo).getId();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArquivosModel buscarImagemPet(Long id) {
        UUID uuid = this.petRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Pet não encontrado!")).getImagem();
        return this.arquivoRepository.findById(uuid).orElseThrow(() -> new ObjectNotFoundException("Arquivo não encontrado!"));
    }

    @Override
    @Transactional
    public void editarPet(Long idPet, Long idCliente, EditarPetForm form, MultipartFile arquivo) {
        PetModel pet = this.getPetPorId(idPet);
        UsuarioModel cliente = this.getUsuarioPorId(idCliente);
        if (!pet.getTutor().equals(cliente)) throw new RuntimeException("Você não é o tutor do pet!");
        if (!pet.getNome().equals(form.getNome()))
            pet.setNome(form.getNome());
        if (!pet.getRaca().equals(form.getRaca()))
            pet.setRaca(form.getRaca());
        if (!pet.getObservacoes().equals(form.getObservacoes()))
            pet.setObservacoes(form.getObservacoes());
        if (!pet.getTipo().equals(form.getTipoAnimal()))
            pet.setTipo(form.getTipoAnimal());
        if (!pet.getGenero().equals(form.getGenero()))
            pet.setGenero(form.getGenero());
        if (!pet.getDataNascimento().equals(form.getDataNascimento()) && form.getDataNascimento() != null)
            pet.setDataNascimento(form.getDataNascimento());
        if (arquivo != null && !arquivo.isEmpty()) {
            UUID imagemAntiga = pet.getImagem();
            UUID novaImagem = this.salvarArquivo(arquivo);
            pet.setImagem(novaImagem);
            if (imagemAntiga != null) {
                this.arquivoRepository.deleteById(imagemAntiga);
            }
        }
        this.petRepository.save(pet);
    }

    @Override
    public PetPodeSerDeletadoDto verificarPetPodeSerDeletado(Long idPet, Long idCliente) {
        PetModel pet = this.getPetPorId(idPet);
        UsuarioModel cliente = this.getUsuarioPorId(idCliente);
        if (!pet.getTutor().equals(cliente)) throw new RuntimeException("Você não é o tutor do pet em questão!");
        List<ConsultaModel> consultas = this.consultaRepository.buscarConsultasPendenteOuIniciadas(idPet);
        boolean possuiConsultasIniciadas = consultas.stream().anyMatch(consulta -> consulta.getStatus().equals(StatusConsultaEnum.INICIADO));
        return new PetPodeSerDeletadoDto(possuiConsultasIniciadas, consultas);
    }

    @Override
    @Transactional
    public void deletarPet(Long idPet, Long idCliente) {
        PetModel pet = this.getPetPorId(idPet);
        UsuarioModel cliente = this.getUsuarioPorId(idCliente);
        if (!pet.getTutor().equals(cliente)) throw new RuntimeException("Você não é o tutor do pet em questão!");
        if (this.consultaRepository.existsByPet_IdAndStatus(idPet, StatusConsultaEnum.INICIADO))
            throw new RuntimeException("O pet não pode ser deletado, já que o mesmo se encontra em consulta no momento!");
        pet.setStatus(StatusPerfilEnum.D);
        this.cancelarConsultas(idPet);
        this.petRepository.save(pet);
    }

    @Transactional
    protected void cancelarConsultas(Long idPet) {
        List<ConsultaModel> consultasPendentes = this.consultaRepository.findAllByPet_IdAndStatus(idPet, StatusConsultaEnum.PENDENTE);
        for (ConsultaModel consulta : consultasPendentes) {
            consulta.setStatus(StatusConsultaEnum.CANCELADO);
            consulta.setCanceladoEm(LocalDateTime.now());
            consulta.setMotivoCancelamento("O pet foi desativado pelo seu tutor.");
            this.consultaRepository.save(consulta);
        }
    }

    @Override
    public String gerarCarteirinha(Long idPet, Model model) {
        CarteirinhaDto pet = new CarteirinhaDto(this.getPetPorId(idPet));
        Context context = new Context();
        context.setVariable("pet", pet);
        String base64 = Base64.getEncoder()
                .encodeToString(this.buscarImagemPet(idPet).getConteudo());

        context.setVariable("imagemBase64", base64);
        return templateEngine.process("carteirinha", context);
    }

    @Override
    public byte[] baixarCarteirinha(Long idPet) {
        CarteirinhaDto pet = new CarteirinhaDto(this.getPetPorId(idPet));
        Context context = new Context();
        context.setVariable("pet", pet);
        String base64 = Base64.getEncoder()
                .encodeToString(this.buscarImagemPet(idPet).getConteudo());
        context.setVariable("imagemBase64", base64);
        String html = templateEngine.process("carteirinha", context);
        return RelatoriosUtils.getBytes(html);
    }

    @Override
    public List<MeuPetConsultaDto> listarPetsConsultas(Long idUsuario) {
        List<ConsultaModel> consultas = this.consultaRepository.findAllBySolicitante_Id(idUsuario).stream().filter(consulta -> consulta.getStatus() != StatusConsultaEnum.CANCELADO && consulta.getStatus() != StatusConsultaEnum.FINALIZADO && consulta.getStatus() != StatusConsultaEnum.REPROVADA).toList();
        return MeuPetConsultaDto.convert(consultas);
    }

    @Override
    public List<MinhasConsultasDto> listarConsultasPet(Long idPet) {
        List<ConsultaModel> consultas = this.consultaRepository.findAllByPet_IdOrderByDataConsultaDesc(idPet);
        return MinhasConsultasDto.convert(consultas);
    }

    public PetInformacoesDto buscarInformacoesDePet(Long idUsuario, Long idPet) {
        PetModel pet = this.getPetPorId(idPet);
        List<PetModel> petsRelacionados = this.petRepository.buscarPetsRelacionados(idUsuario, pet.getGenero(), pet.getTipo(), pet.getId());
        return new PetInformacoesDto(pet, petsRelacionados);
    }
}
