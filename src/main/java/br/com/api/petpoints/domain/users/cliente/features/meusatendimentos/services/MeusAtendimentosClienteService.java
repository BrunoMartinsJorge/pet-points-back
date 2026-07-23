package br.com.api.petpoints.domain.users.cliente.features.meusatendimentos.services;

import br.com.api.petpoints.domain.users.cliente.features.meusatendimentos.dto.MeusAtendimentosDto;

import java.util.List;

public interface MeusAtendimentosClienteService {
    List<MeusAtendimentosDto> listarMeusAtendimentos(Long idUsuario);
}
