package br.com.api.petpoints.shared.features.arquivos.service;

import br.com.api.petpoints.shared.models.ArquivosModel;

import java.util.UUID;

public interface ArquivosService {
    ArquivosModel buscarArquivoPorUUID(UUID uuid);
    ArquivosModel buscarArquivoPorIdUsuario(Long idUsuario);
}
