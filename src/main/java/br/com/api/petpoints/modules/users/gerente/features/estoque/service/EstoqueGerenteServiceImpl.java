package br.com.api.petpoints.modules.users.gerente.features.estoque.service;

import br.com.api.petpoints.modules.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.modules.users.estoquista.features.estoque.dto.ProdutoRelatorioDto;
import br.com.api.petpoints.modules.users.gerente.features.estoque.dto.ProdutoDto;
import br.com.api.petpoints.modules.users.gerente.features.estoque.form.FiltrosProdutoForm;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.models.MovimentacaoModel;
import br.com.api.petpoints.shared.models.ProdutoModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.ProdutoRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import br.com.api.petpoints.shared.utils.ColunaRelatorio;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import br.com.api.petpoints.shared.utils.RelatoriosUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EstoqueGerenteServiceImpl implements EstoqueGerenteService {

    private final ProdutoRepository produtoRepository;
    private final RelatoriosUtils relatoriosUtils;
    private final UsuarioRepository usuarioRepository;

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

    @Override
    public byte[] gerarRelatorioProdutos(FiltrosProdutoForm form) {
        List<ProdutoRelatorioDto> produtos = this.filtrarProdutosRelatorios(form, this.produtoRepository.findAll());
        String titulo = "Relatório de Produtos em Estoque";
        List<ColunaRelatorio> colunas = List.of(
                new ColunaRelatorio("Produto", m -> ((ProdutoRelatorioDto) m).getNome()),
                new ColunaRelatorio("Descrição", m -> ((ProdutoRelatorioDto) m).getDescricao()),
                new ColunaRelatorio("Tipo", m -> ((ProdutoRelatorioDto) m).getTipo()),
                new ColunaRelatorio("Valor Unitário", m -> ((ProdutoRelatorioDto) m).getValorUnitario()),
                new ColunaRelatorio("Quantidade em Estoque", m -> ((ProdutoRelatorioDto) m).getQuantidadeEstoque())
        );
        return this.relatoriosUtils.gerarRelatorioGenerico(colunas, produtos, titulo, "");
    }


    private List<ProdutoRelatorioDto> filtrarProdutosRelatorios(FiltrosProdutoForm form, List<ProdutoModel> produtos) {
        Stream<ProdutoModel> stream = produtos.stream();

        if (form.getNome() != null && !form.getNome().isEmpty()) {
            stream = stream.filter(produto -> produto.getNome().contains(form.getNome()));
        }

        if (!Objects.equals(form.getTipoProduto(), "") && form.getTipoProduto() != null) {
            stream = stream.filter(produto ->
                    Objects.equals(produto.getTipo().toString(), form.getTipoProduto())
            );
        }

        if (form.getPrecoMin() != null) {
            stream = stream.filter(produto ->
                    produto.getValorUnitario() >= form.getPrecoMin()
            );
        }

        if (form.getPrecoMax() != null) {
            stream = stream.filter(produto ->
                    produto.getValorUnitario() <= form.getPrecoMax()
            );
        }

        return ProdutoRelatorioDto.convert(stream.toList());
    }
}
