package br.com.api.petpoints.modules.cliente.features.meuperfil.service;

import br.com.api.petpoints.modules.cliente.features.meuperfil.dto.MeuPerfilDto;
import br.com.api.petpoints.modules.cliente.features.meuperfil.forms.EditarPerfilClienteForm;
import br.com.api.petpoints.modules.usuario.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MeuPerfilClienteServiceImpl implements MeuPerfilClienteService {

    private final UsuarioRepository usuarioRepository;
    private final ConsultaRepository consultaRepository;

    private UsuarioModel getUsuarioPorId(Long idUsuario) {
        return this.usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioNaoEncontrado("Não foi encontrado usuário com ID: " + idUsuario + "!"));
    }

    @Override
    public MeuPerfilDto buscarPerfilDeCliente(Long idUsuario) {
        UsuarioModel cliente = this.getUsuarioPorId(idUsuario);
        return new MeuPerfilDto(cliente);
    }

    @Override
    public void editarPerfilCliente(Long idUsuario, EditarPerfilClienteForm form) {
        UsuarioModel cliente = this.alterarInformacoesComFormulario(this.getUsuarioPorId(idUsuario), form);
        this.usuarioRepository.save(cliente);
    }

    private UsuarioModel alterarInformacoesComFormulario(UsuarioModel cliente, EditarPerfilClienteForm form) {
        cliente.setEmail(Objects.equals(cliente.getEmail(), form.getEmail()) || (form.getEmail().isBlank() || form.getEmail().isEmpty()) ? cliente.getEmail() : form.getEmail());
        cliente.setNome(Objects.equals(cliente.getNome(), form.getNome()) || (form.getNome().isBlank() || form.getNome().isEmpty()) ? cliente.getNome() : form.getNome());
        cliente.setCpf(Objects.equals(cliente.getCpf(), form.getCpf()) || (form.getCpf().isBlank() || form.getCpf().isEmpty()) ? cliente.getCpf() : form.getCpf());
        cliente.setTelefone(Objects.equals(cliente.getTelefone(), form.getTelefone()) || (form.getTelefone().isBlank() || form.getTelefone().isEmpty()) ? cliente.getTelefone() : form.getTelefone());
        cliente.setGenero(Objects.equals(cliente.getGenero(), form.getGenero()) || form.getGenero() == null ? cliente.getGenero() : form.getGenero());
        cliente.setDataNascimento(Objects.equals(cliente.getDataNascimento(), form.getDataNascimento()) || form.getDataNascimento() == null ? cliente.getDataNascimento() : form.getDataNascimento());
        return cliente;
    }

    @Override
    public void desativarPerfilDeCliente(Long idUsuario) {
        UsuarioModel cliente = this.getUsuarioPorId(idUsuario);

    }
}
