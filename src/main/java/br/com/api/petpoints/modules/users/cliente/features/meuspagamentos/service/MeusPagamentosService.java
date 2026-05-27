package br.com.api.petpoints.modules.users.cliente.features.meuspagamentos.service;

import br.com.api.petpoints.modules.users.cliente.features.meuspagamentos.dto.PagamentosPendentesDto;

import java.util.List;

public interface MeusPagamentosService {
    List<PagamentosPendentesDto> listarPagamentosPendentes(Long idUsuario);
}
