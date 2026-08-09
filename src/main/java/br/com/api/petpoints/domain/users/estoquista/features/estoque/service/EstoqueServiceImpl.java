package br.com.api.petpoints.domain.users.estoquista.features.estoque.service;

import br.com.api.petpoints.domain.users.estoquista.features.estoque.dto.CardsEstoqueDto;
import br.com.api.petpoints.domain.users.estoquista.features.estoque.dto.ProdutoDetalhesDto;
import br.com.api.petpoints.domain.users.estoquista.features.estoque.form.EditarProdutoForm;
import br.com.api.petpoints.domain.users.estoquista.shared.dto.ProdutoEstoqueDto;
import br.com.api.petpoints.domain.users.estoquista.features.estoque.dto.ProdutoRelatorioDto;
import br.com.api.petpoints.shared.enums.TipoLogEnum;
import br.com.api.petpoints.shared.features.logs.LogsServiceImpl;
import br.com.api.petpoints.shared.form.FiltrosProdutoForm;
import br.com.api.petpoints.domain.users.estoquista.features.estoque.form.NovoProdutoForm;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.models.LogsModel;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(EstoqueServiceImpl.class);
    private final ProdutoRepository produtoRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final TemplateEngine templateEngine;
    private final LogsServiceImpl logsService;
    private final UsuarioRepository usuarioRepository;

    private List<ProdutoModel> getProdutos() {
        return this.produtoRepository.findAll();
    }

    private ProdutoModel getProdutoPorId(Long idProduto) {
        return this.produtoRepository.findById(idProduto).orElseThrow(() -> new ObjectNotFoundException("Produto com ID: " + idProduto + " não encontrado!"));
    }

    private UsuarioModel getUsuarioPorId(Long idUsuario) {
        return this.usuarioRepository.findById(idUsuario).orElseThrow(() -> new ObjectNotFoundException("Usuário com ID: " + idUsuario + " não encontrado!"));
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
            if (produto.abaixoEstoque())
                quantidadeAbaixoEstoque++;
            valorTotal += produto.valorEstoque();
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
    public void registrarProduto(Long idUsuario, NovoProdutoForm form) {
        UsuarioModel estoquista = this.getUsuarioPorId(idUsuario);
        log.info("[REGISTRO DE PRODUTO] - ESTOQUISTA - Estoquista {} esta registrando um produto com as informações {}", estoquista.getNome(), form.toString());
        ProdutoModel produto = this.produtoRepository.save(new ProdutoModel(form));
        log.info("[REGISTRO DE PRODUTO] - ESTOQUISTA - Produto registrado com sucesso - ID: {}", produto.getId());
        String apendice = "Produto registrado: " + produto.getNome() + " - ID: " + produto.getId();
        this.logsService.registrarLog(estoquista, TipoLogEnum.REGISTROU_PRODUTO, apendice);
        log.info("[REGISTRO DE PRODUTO] - ESTOQUISTA - Registrando Log de Criação de Produto");
    }

    @Override
    @Transactional
    public void editarProduto(Long idUsuario, EditarProdutoForm form, Long idProduto) {
        UsuarioModel estoquista = this.getUsuarioPorId(idUsuario);
        ProdutoModel produto = this.getProdutoPorId(idProduto);
        produto.setValorUnitario(form.getValorUnitario());
        produto.setQuantidadeMinima(form.getQuantidadeAbaixoEstoque());
        produto.setTipo(form.getTipo());
        produto.setNome(form.getNome());
        produto.setDescricao(form.getDescricao());
        this.produtoRepository.save(produto);
        String apendice = "Produto alterado: " + produto.getNome() + " - ID: " + produto.getId();
        this.logsService.registrarLog(estoquista, TipoLogEnum.EDITOU_PRODUTO, apendice);
    }

    @Override
    @Transactional
    public void removerProduto(Long idUsuario, Long idProduto) {
        UsuarioModel estoquista = this.getUsuarioPorId(idUsuario);
        ProdutoModel produto = this.getProdutoPorId(idProduto);
        this.produtoRepository.deleteById(idProduto);
        String apendice = "Produto removido: " + produto.getNome() + " - ID: " + produto.getId();
        this.logsService.registrarLog(estoquista, TipoLogEnum.REMOVEU_PRODUTO, apendice);
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
