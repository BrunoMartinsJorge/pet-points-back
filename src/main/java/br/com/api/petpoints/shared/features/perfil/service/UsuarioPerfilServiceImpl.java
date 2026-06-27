package br.com.api.petpoints.shared.features.perfil.service;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.modules.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.shared.features.perfil.dto.InformacoesUsuarioDto;
import br.com.api.petpoints.shared.features.perfil.form.EditarPerfilForm;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsuarioPerfilServiceImpl implements UsuarioPerfilService {

    private final UsuarioRepository usuarioRepository;
    private final ArquivoRepository arquivoRepository;
    private final ConsultaRepository consultaRepository;
    private final PetRepository petRepository;

    private UsuarioModel getUsuarioPorId(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: "
                + id + " não encontrado!"));
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
    public void editarInformacoesUsuario(Long idUsuario, EditarPerfilForm form, MultipartFile imagem) {
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
        log.info("[DESATIVAR PERFIL] - Iniciando processo de desativar perfil de usuário");
        UsuarioModel usuario = this.getUsuarioPorId(idUsuario);
        log.info("Usuário encontrado - " + usuario.getNome() + " - " + usuario.getCpf());
        switch (usuario.getPermissao()) {
            case TipoUsuario.C -> this.desativarPerfilCliente(usuario);
            case TipoUsuario.A -> this.desativarPerfilCliente(usuario);
            case TipoUsuario.E -> this.desativarPerfilCliente(usuario);
            case TipoUsuario.G -> this.desativarPerfilCliente(usuario);
            case TipoUsuario.V -> this.desativarPerfilCliente(usuario);
        }
        usuario.setStatusPerfilEnum(StatusPerfilEnum.D);
        this.usuarioRepository.save(usuario);
        log.info("[DESATIVAR PERFIL] - Operação de desativação de perfil finalizada! Perfil desabilitado!");
    }

    @Transactional
    protected void desativarPerfilCliente(UsuarioModel cliente) {
        log.info("[DESATIVAR PERFIL - CLIENTE] - Iniciando operações expecificas de desativar perfil de cliente");
        List<ConsultaModel> consultas = this.consultaRepository.findAllBySolicitante_Id(cliente.getId());
        log.info("Listando consultas do cliente. {} consultas", consultas.size());
        List<ConsultaModel> consultasFinalizadas = consultas.stream().filter(consulta -> consulta.getStatus().equals(StatusConsultaEnum.FINALIZADO) && !consulta.getPagamento().getStatusPagamento().equals(StatusPagamentoEnum.APROVADO)).toList();
        log.info("Consultas filtradas para cancelamento. {} consultas", consultas.size());
        if (!consultasFinalizadas.isEmpty()) throw new RuntimeException("Cliente possui pagamentos pendentes!");
        consultas.forEach(consulta -> {
            if (consulta.getStatus().equals(StatusConsultaEnum.FINALIZADO)) return;
            log.info("Alterando status da consulta #{} para finalizado", consulta.getId());
            consulta.setStatus(StatusConsultaEnum.CANCELADO);
            consulta.setCanceladoEm(LocalDateTime.now());
            consulta.setMotivoCancelamento("Cliente desativou seu perfil!");
            this.consultaRepository.save(consulta);
        });
        List<PetModel> pets = this.petRepository.findAllByTutor_Id(cliente.getId()).stream().filter(pet -> pet.getStatus().equals(StatusPerfilEnum.A)).toList();
        log.info("Listando pets do cliente para serem desativados. {} Pets", pets.size());
        pets.forEach(pet -> {
            log.info("Alterando status do pet #{} - {} para DESATIVADO", pet.getId(), pet.getNome());
            pet.setStatus(StatusPerfilEnum.D);
            this.petRepository.save(pet);
        });
        log.info("[DESATIVAR PERFIL - CLIENTE] - Finalizada todas as operações expecificas de desativar perfil de cliente");
    }
}
