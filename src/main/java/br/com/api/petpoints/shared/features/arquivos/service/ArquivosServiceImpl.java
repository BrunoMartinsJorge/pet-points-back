package br.com.api.petpoints.shared.features.arquivos.service;

import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.models.ArquivosModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.ArquivoRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArquivosServiceImpl implements ArquivosService {

    private final ArquivoRepository arquivoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public ArquivosModel buscarArquivoPorUUID(UUID uuid) {
        return this.arquivoRepository.findById(uuid).orElseThrow(() -> new ObjectNotFoundException("Arquivo com UUID não encontrado!"));
    }

    @Override
    public ArquivosModel buscarArquivoPorIdUsuario(Long idUsuario) {
        UsuarioModel usuario = this.usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: " + idUsuario + " não encontrado!"));
        if (usuario.getImagem() == null) return null;
        return this.buscarArquivoPorUUID(usuario.getImagem());
    }
}
