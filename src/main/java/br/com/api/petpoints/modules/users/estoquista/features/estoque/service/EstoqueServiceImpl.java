package br.com.api.petpoints.modules.users.estoquista.features.estoque.service;

import br.com.api.petpoints.modules.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.modules.users.estoquista.features.estoque.dto.CardsEstoqueDto;
import br.com.api.petpoints.modules.users.estoquista.features.estoque.dto.ProdutoDetalhesDto;
import br.com.api.petpoints.modules.users.estoquista.features.estoque.dto.ProdutoEstoqueDto;
import br.com.api.petpoints.modules.users.estoquista.features.estoque.dto.ProdutoRelatorioDto;
import br.com.api.petpoints.modules.users.estoquista.features.estoque.form.FiltrosProdutoForm;
import br.com.api.petpoints.modules.users.estoquista.features.estoque.form.NovaMovimentacaoForm;
import br.com.api.petpoints.modules.users.estoquista.features.estoque.form.NovoProdutoForm;
import br.com.api.petpoints.shared.enums.TipoMovimentacaoEnum;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.models.MovimentacaoModel;
import br.com.api.petpoints.shared.models.ProdutoModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.MovimentacaoRepository;
import br.com.api.petpoints.shared.repository.ProdutoRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import br.com.api.petpoints.shared.utils.RelatoriosUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;
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
    public ProdutoDetalhesDto buscarDetalhesProdutosEstoque(Long idProduto, Long idUsuario) {
        ProdutoModel produto = this.getProdutoPorId(idProduto);
        List<MovimentacaoModel> movimentacoes = this.movimentacaoRepository.findAllByProduto_IdAndMovimentadoPor_Id(idProduto, idUsuario);
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

    @Override
    @Transactional
    public void realizarMovimentacao(NovaMovimentacaoForm form, Long idUsuario) {
        ProdutoModel produto = this.getProdutoPorId(form.getIdProduto());
        UsuarioModel usuario = this.usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioNaoEncontrado("O usuário com ID: " + idUsuario + " não foi encontrado!"));
        if (form.getTipoMovimentacao() == TipoMovimentacaoEnum.SAIDA && (produto.getQuantidadeEstoque() < form.getQuantidadeMovimentada()))
            throw new RuntimeException("O produto não possui quantidade suficiente para realizar a saida!"); // Illegal
        int quantidade = form.getTipoMovimentacao() == TipoMovimentacaoEnum.ENTRADA ? produto.getQuantidadeEstoque() + form.getQuantidadeMovimentada() : produto.getQuantidadeEstoque() - form.getQuantidadeMovimentada();
        produto.setQuantidadeEstoque(quantidade);
        this.produtoRepository.save(produto);
        this.movimentacaoRepository.save(new MovimentacaoModel(form, usuario, produto));
    }

    private List<ProdutoRelatorioDto> filtrarProdutosRelatorios(FiltrosProdutoForm form, List<ProdutoModel> produtos) {
        Stream<ProdutoModel> stream = produtos.stream();

        if (form.getNome() != null && !form.getNome().isEmpty()) {
            stream = stream.filter(produto -> produto.getNome().contains(form.getNome()));
        }

        if (form.getTipoProduto() != null) {
            stream = stream.filter(produto ->
                    produto.getTipo() == form.getTipoProduto()
            );
        }

        if (form.isQuantidadeAbaixoEstoque()) {
            stream = stream.filter(produto ->
                    produto.getQuantidadeEstoque() < produto.getQuantidadeMinima()
            );
        }

        stream = stream.filter(produto ->
                produto.getValorUnitario() >= form.getPrecoMin()
        );

        if (form.getPrecoMax() > 0) {
            stream = stream.filter(produto ->
                    produto.getValorUnitario() <= form.getPrecoMax()
            );
        }

        return ProdutoRelatorioDto.convert(stream.toList());
    }
}
