package br.com.api.petpoints.domain.users.estoquista.features.movimentacoes.service;

import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.domain.users.estoquista.features.movimentacoes.dto.MinhasMovimentacoesDto;
import br.com.api.petpoints.shared.dto.ProdutoFiltroDto;
import br.com.api.petpoints.domain.users.estoquista.features.movimentacoes.form.NovaMovimentacaoForm;
import br.com.api.petpoints.domain.users.estoquista.features.movimentacoes.form.RelatorioMovimentacoesForm;
import br.com.api.petpoints.shared.enums.TipoMovimentacaoEnum;
import br.com.api.petpoints.shared.exception.custom.IllegalAccessException;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.models.MovimentacaoModel;
import br.com.api.petpoints.shared.models.ProdutoModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.MovimentacaoRepository;
import br.com.api.petpoints.shared.repository.ProdutoRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import br.com.api.petpoints.shared.utils.ColunaRelatorio;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import br.com.api.petpoints.shared.utils.RelatoriosUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MinhasMovimentacoesServiceImpl implements MinhasMovimentacoesService {

    private final UsuarioRepository usuarioRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final RelatoriosUtils relatoriosUtils;
    private final ProdutoRepository produtoRepository;

    private ProdutoModel getProdutoPorId(Long idProduto) {
        return this.produtoRepository.findById(idProduto).orElseThrow(() -> new ObjectNotFoundException("Produto com ID: " + idProduto + " não encontrado!"));
    }

    @Override
    public List<MinhasMovimentacoesDto> listarMovimentacoesEstoquista(Long idUsuario) {
        return MinhasMovimentacoesDto.convert(this.movimentacaoRepository.findAllByMovimentadoPor_Id(idUsuario));
    }

    @Override
    public List<ProdutoFiltroDto> buscarProdutosParaFiltro() {
        return ProdutoFiltroDto.convert(this.produtoRepository.findAll());
    }

    @Override
    public byte[] gerarRelatorioMovimentacoes(Long idUsuario, RelatorioMovimentacoesForm form) {
        UsuarioModel usuario = this.usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: " + idUsuario + " não encontrado!"));
        List<MovimentacaoModel> movimentacoes = this.movimentacaoRepository.findAllByMovimentadoPor_Id(idUsuario);
        String titulo = "Relatório de Movimentações";
        String subtitulo = "Movimentações Realizadas por " + usuario.getNome();
        List<ColunaRelatorio> colunas = List.of(
                new ColunaRelatorio("ID", m -> ((MovimentacaoModel) m).getId()),
                new ColunaRelatorio("Tipo", m -> ((MovimentacaoModel) m).getTipo()),
                new ColunaRelatorio("Data Hora", m -> (LocalDateTimeUtils.converterLocalDateTimeParaPtBr(((MovimentacaoModel) m).getMovimentadoEm()))),
                new ColunaRelatorio("Quantidade", m -> (((MovimentacaoModel) m).getQuantidadeMovimentada() + " Unidades")),
                new ColunaRelatorio("Produto", m -> ((MovimentacaoModel) m).getProduto().getNome())
        );
        return this.relatoriosUtils.gerarRelatorioGenerico(colunas, movimentacoes, titulo, subtitulo);
    }

    @Override
    @Transactional
    public void realizarMovimentacao(NovaMovimentacaoForm form, Long idUsuario) {
        ProdutoModel produto = this.getProdutoPorId(form.getIdProduto());
        UsuarioModel usuario = this.usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioNaoEncontrado("O usuário com ID: " + idUsuario + " não foi encontrado!"));
        if (form.getTipoMovimentacao() == TipoMovimentacaoEnum.SAIDA && (produto.getQuantidadeEstoque() < form.getQuantidadeMovimentada()))
            throw new IllegalAccessException("O produto não possui quantidade suficiente para realizar a saida!");
        int quantidade = form.getTipoMovimentacao() == TipoMovimentacaoEnum.ENTRADA ? produto.getQuantidadeEstoque() + form.getQuantidadeMovimentada() : produto.getQuantidadeEstoque() - form.getQuantidadeMovimentada();
        produto.setQuantidadeEstoque(quantidade);
        this.produtoRepository.save(produto);
        this.movimentacaoRepository.save(new MovimentacaoModel(form, usuario, produto));
    }
}