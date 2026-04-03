package br.com.api.petpoints.modules.gerente.features.estoque.service;

import br.com.api.petpoints.modules.gerente.features.estoque.dto.ProdutoDto;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.models.ProdutoModel;
import br.com.api.petpoints.shared.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstoqueGerenteServiceImpl implements EstoqueGerenteService {

    private final ProdutoRepository produtoRepository;

    @Override
    public List<ProdutoDto> listarProdutos() {
        List<ProdutoModel> produtos = produtoRepository.findAll();
        return produtos.stream().map(ProdutoDto::new).toList();
    }

    @Override
    public ProdutoDto buscarProdutoPorId(Long idProduto) {
        ProdutoModel produto = produtoRepository.findById(idProduto).orElseThrow(() -> new ObjectNotFoundException("Produto com ID: " + idProduto + " não encontrado"));
        return new ProdutoDto(produto);
    }

    @Override
    public List<ProdutoDto> buscarProdutosComEstoqueAbaixoDoLimite() {
        List<ProdutoModel> produtos = produtoRepository.listarProdutosAbaixoDeEstoque();
        return produtos.stream().map(ProdutoDto::new).toList();
    }
}
