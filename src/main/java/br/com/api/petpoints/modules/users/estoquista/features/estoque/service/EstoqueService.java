package br.com.api.petpoints.modules.users.estoquista.features.estoque.service;

import br.com.api.petpoints.modules.users.estoquista.features.estoque.dto.CardsEstoqueDto;
import br.com.api.petpoints.modules.users.estoquista.features.estoque.dto.ProdutoDetalhesDto;
import br.com.api.petpoints.modules.users.estoquista.features.estoque.dto.ProdutoEstoqueDto;
import br.com.api.petpoints.modules.users.estoquista.features.estoque.form.FiltrosProdutoForm;
import br.com.api.petpoints.modules.users.estoquista.features.estoque.form.NovaMovimentacaoForm;
import br.com.api.petpoints.modules.users.estoquista.features.estoque.form.NovoProdutoForm;

import java.util.List;

public interface EstoqueService {

    List<ProdutoEstoqueDto> listarProdutosEstoque();
    CardsEstoqueDto gerarCardsEstoque();
    ProdutoDetalhesDto buscarDetalhesProdutosEstoque(Long idProduto, Long idUsuario);
    byte[] gerarRelatorioProdutos(FiltrosProdutoForm form);
    void registrarProduto(NovoProdutoForm form);
    void realizarMovimentacao(NovaMovimentacaoForm form, Long idUsuario);
}
