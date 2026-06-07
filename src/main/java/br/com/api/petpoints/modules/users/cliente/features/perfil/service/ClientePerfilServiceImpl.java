package br.com.api.petpoints.modules.users.cliente.features.perfil.service;

import br.com.api.petpoints.modules.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.modules.users.cliente.features.perfil.dto.InformacoesUsuarioDto;
import br.com.api.petpoints.modules.users.cliente.features.perfil.form.EditarPerfilFotm;
import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import br.com.api.petpoints.shared.enums.StatusPerfilEnum;
import br.com.api.petpoints.shared.models.ArquivosModel;
import br.com.api.petpoints.shared.models.ConsultaModel;
import br.com.api.petpoints.shared.models.PetModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.ArquivoRepository;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import br.com.api.petpoints.shared.repository.PetRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientePerfilServiceImpl implements ClientePerfilService {

    private final UsuarioRepository usuarioRepository;
    private final ArquivoRepository arquivoRepository;
    private final ConsultaRepository consultaRepository;
    private final PetRepository petRepository;

    private UsuarioModel getUsuarioPorId(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: " + id + " não encontrado!"));
    }

    @Override
    public InformacoesUsuarioDto buscarInformacoes(Long idUsuario) {
        return new InformacoesUsuarioDto(this.getUsuarioPorId(idUsuario));
    }

    private UUID salvarArquivo(MultipartFile form) {
        if (form.getSize() > 5_000_000) throw new RuntimeException("Arquivo passa de 5MB!");
        List<String> tiposPermitidos = List.of("image/png", "image/jpeg", "application/pdf");
        if (!tiposPermitidos.contains(form.getContentType())) throw new RuntimeException("Tipo inválido");
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
    @Transactional
    public void editarInformacoesUsuario(Long idUsuario, EditarPerfilFotm form, MultipartFile imagem) {
        UsuarioModel usuario = this.getUsuarioPorId(idUsuario);
        usuario.setNome(form.getNome());
        usuario.setEmail(form.getEmail());
        usuario.setCpf(form.getCpf());
        usuario.setDataNascimento(form.getDataNascimento());
        usuario.setTelefone(form.getTelefone());
        usuario.setGenero(form.getGenero());
        if (imagem != null) {
            if (usuario.getImagem() != null) {
                UUID antigo = usuario.getImagem();
                UUID novo = this.salvarArquivo(imagem);
                usuario.setImagem(novo);
                this.arquivoRepository.deleteById(antigo);
            } else {
                UUID novo = this.salvarArquivo(imagem);
                usuario.setImagem(novo);
            }
        }
        this.usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void desativarPerfil(Long idUsuario) {
        List<ConsultaModel> consultas = this.consultaRepository.findAllBySolicitante_Id(idUsuario);
        List<ConsultaModel> consultasFinalizadas = consultas.stream().filter(consulta -> consulta.getStatus().equals(StatusConsultaEnum.FINALIZADO) && !consulta.getPagamento().getStatusPagamento().equals(StatusPagamentoEnum.APROVADO)).toList();
        if (!consultasFinalizadas.isEmpty()) throw new RuntimeException("Cliente possui pagamentos pendentes!");
        consultas.forEach(consulta -> {
            if (consulta.getStatus().equals(StatusConsultaEnum.FINALIZADO)) return;
            consulta.setStatus(StatusConsultaEnum.CANCELADO);
            consulta.setCanceladoEm(LocalDateTime.now());
            consulta.setMotivoCancelamento("Cliente desativou seu perfil!");
            this.consultaRepository.save(consulta);
        });
        List<PetModel> pets = this.petRepository.findAllByTutor_Id(idUsuario).stream().filter(pet -> pet.getStatus().equals(StatusPerfilEnum.A)).toList();
        pets.forEach(pet -> {
            pet.setStatus(StatusPerfilEnum.D);
            this.petRepository.save(pet);
        });
        UsuarioModel cliente = this.getUsuarioPorId(idUsuario);
        cliente.setStatusPerfilEnum(StatusPerfilEnum.D);
        this.usuarioRepository.save(cliente);
    }
}
