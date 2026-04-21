package br.com.api.petpoints.modules.users.gerente.features.estoque.service;

import br.com.api.petpoints.modules.users.gerente.features.estoque.dto.ProdutoDto;
import br.com.api.petpoints.modules.users.gerente.features.estoque.form.FiltrosProdutoForm;

import java.util.List;

public interface EstoqueGerenteService {

    List<ProdutoDto> listarProdutos();
    ProdutoDto buscarProdutoPorId(Long id);
    List<ProdutoDto> buscarProdutosComEstoqueAbaixoDoLimite();
    byte[] gerarRelatorioProdutos(FiltrosProdutoForm form);
}
