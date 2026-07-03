package br.com.api.petpoints.domain.users.estoquista.features.estoque.service;

import br.com.api.petpoints.domain.users.estoquista.features.estoque.dto.CardsEstoqueDto;
import br.com.api.petpoints.domain.users.estoquista.features.estoque.dto.ProdutoDetalhesDto;
import br.com.api.petpoints.domain.users.estoquista.shared.dto.ProdutoEstoqueDto;
import br.com.api.petpoints.shared.form.FiltrosProdutoForm;
import br.com.api.petpoints.domain.users.estoquista.features.estoque.form.NovoProdutoForm;

import java.util.List;

public interface EstoqueService {

    List<ProdutoEstoqueDto> listarProdutosEstoque();
    CardsEstoqueDto gerarCardsEstoque();
    ProdutoDetalhesDto buscarDetalhesProdutosEstoque(Long idProduto);
    byte[] gerarRelatorioProdutos(FiltrosProdutoForm form);
    void registrarProduto(NovoProdutoForm form);
}
