package br.com.api.petpoints.domain.users.estoquista.features.dashboard.service;

import br.com.api.petpoints.domain.users.estoquista.features.dashboard.dto.HistoricoMovimentacoesMensalDto;
import br.com.api.petpoints.domain.users.estoquista.shared.dto.ProdutoEstoqueDto;

import java.util.List;

public interface DashboardEstoquistaService {
    List<HistoricoMovimentacoesMensalDto> listarMovimentacoesMensaisGrafico(Long idUsuario);
    List<ProdutoEstoqueDto> listarProdutosAbaixoEstoque();
}
