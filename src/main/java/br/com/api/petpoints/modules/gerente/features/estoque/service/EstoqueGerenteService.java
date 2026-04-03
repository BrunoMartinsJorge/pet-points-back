package br.com.api.petpoints.modules.gerente.features.estoque.service;

import br.com.api.petpoints.modules.gerente.features.estoque.dto.ProdutoDto;

import java.util.List;

public interface EstoqueGerenteService {

    List<ProdutoDto> listarProdutos();
    ProdutoDto buscarProdutoPorId(Long id);
    List<ProdutoDto> buscarProdutosComEstoqueAbaixoDoLimite();
}
