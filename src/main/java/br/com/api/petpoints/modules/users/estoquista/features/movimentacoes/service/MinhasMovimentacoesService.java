package br.com.api.petpoints.modules.users.estoquista.features.movimentacoes.service;

import br.com.api.petpoints.modules.users.estoquista.features.movimentacoes.dto.MinhasMovimentacoesDto;

import java.util.List;

public interface MinhasMovimentacoesService {
    List<MinhasMovimentacoesDto> listarMovimentacoesEstoquista(Long id);
}
