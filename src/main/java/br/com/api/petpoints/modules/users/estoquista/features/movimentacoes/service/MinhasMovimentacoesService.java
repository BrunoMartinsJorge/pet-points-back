package br.com.api.petpoints.modules.users.estoquista.features.movimentacoes.service;

import br.com.api.petpoints.modules.users.estoquista.features.movimentacoes.dto.MinhasMovimentacoesDto;
import br.com.api.petpoints.modules.users.estoquista.features.movimentacoes.form.RelatorioMovimentacoesForm;

import java.util.List;

public interface MinhasMovimentacoesService {
    List<MinhasMovimentacoesDto> listarMovimentacoesEstoquista(Long id);
    byte[] gerarRelatorioMovimentacoes(Long idUsuario, RelatorioMovimentacoesForm form);
}
