package br.com.api.petpoints.modules.users.estoquista.features.estoque.service;

import br.com.api.petpoints.modules.users.estoquista.features.estoque.dto.CardsEstoqueDto;
import br.com.api.petpoints.modules.users.estoquista.features.estoque.dto.ProdutoDetalhesDto;
import br.com.api.petpoints.modules.users.estoquista.shared.dto.ProdutoEstoqueDto;
import br.com.api.petpoints.modules.users.estoquista.features.estoque.dto.ProdutoRelatorioDto;
import br.com.api.petpoints.shared.form.FiltrosProdutoForm;
import br.com.api.petpoints.modules.users.estoquista.features.estoque.form.NovoProdutoForm;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.models.MovimentacaoModel;
import br.com.api.petpoints.shared.models.ProdutoModel;
import br.com.api.petpoints.shared.repository.MovimentacaoRepository;
import br.com.api.petpoints.shared.repository.ProdutoRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
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
public class EstoqueServiceImpl implements EstoqueService {

    private final ProdutoRepository produtoRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final TemplateEngine templateEngine;
    private final UsuarioRepository usuarioRepository;

    private List<ProdutoModel> getProdutos() {
        return this.produtoRepository.findAll();
    }

    private ProdutoModel getProdutoPorId(Long idProduto) {
        return this.produtoRepository.findById(idProduto).orElseThrow(() -> new ObjectNotFoundException("Produto com ID: " + idProduto + " não encontrado!"));
    }

    @Override
    public List<ProdutoEstoqueDto> listarProdutosEstoque() {
        return ProdutoEstoqueDto.convert(this.getProdutos());
    }

    @Override
    public CardsEstoqueDto gerarCardsEstoque() {
        List<ProdutoModel> produtos = this.getProdutos();
        double valorTotal = 0.0;
        int quantidadeEstoque = 0;
        int quantidadeAbaixoEstoque = 0;
        for (ProdutoModel produto : produtos) {
            quantidadeEstoque += produto.getQuantidadeEstoque();
            if (produto.getQuantidadeEstoque() < produto.getQuantidadeMinima()) {
                quantidadeAbaixoEstoque++;
            }
            valorTotal += produto.getValorUnitario() * produto.getQuantidadeEstoque();
        }
        return new CardsEstoqueDto(valorTotal, quantidadeEstoque, quantidadeAbaixoEstoque);
    }

    @Override
    public ProdutoDetalhesDto buscarDetalhesProdutosEstoque(Long idProduto) {
        ProdutoModel produto = this.getProdutoPorId(idProduto);
        List<MovimentacaoModel> movimentacoes = this.movimentacaoRepository.findAllByProduto_Id(idProduto);
        return new ProdutoDetalhesDto(produto, movimentacoes);
    }

    @Override
    public byte[] gerarRelatorioProdutos(FiltrosProdutoForm form) {
        List<ProdutoRelatorioDto> produtos = this.filtrarProdutosRelatorios(form, this.getProdutos());
        Context context = new Context();
        context.setVariable("produtos", produtos);
        context.setVariable("dataGeracao", LocalDateTimeUtils.converterLocalDateTimeParaPtBr(LocalDateTime.now()));
        String html = templateEngine.process("relatorios/RelatorioProdutos", context);
        return RelatoriosUtils.getBytes(html);
    }

    @Override
    public void registrarProduto(NovoProdutoForm form) {
        this.produtoRepository.save(new ProdutoModel(form));
    }

    private List<ProdutoRelatorioDto> filtrarProdutosRelatorios(FiltrosProdutoForm form, List<ProdutoModel> produtos) {
        Stream<ProdutoModel> stream = produtos.stream();

        if (form.getNome() != null && !form.getNome().isEmpty()) {
            stream = stream.filter(produto -> produto.getNome().contains(form.getNome()));
        }

        if (!Objects.equals(form.getTipoProduto(), "")) {
            stream = stream.filter(produto ->
                    Objects.equals(produto.getTipo().toString(), form.getTipoProduto())
            );
        }

        if (!form.isTodosOsProdutos()) {
            stream = stream.filter(produto ->
                    produto.getQuantidadeEstoque() < produto.getQuantidadeMinima()
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
