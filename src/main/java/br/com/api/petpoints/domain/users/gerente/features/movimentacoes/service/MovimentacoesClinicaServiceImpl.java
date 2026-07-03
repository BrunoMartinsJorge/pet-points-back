package br.com.api.petpoints.domain.users.gerente.features.movimentacoes.service;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.domain.users.gerente.features.movimentacoes.dto.LancadoPorDto;
import br.com.api.petpoints.domain.users.gerente.features.movimentacoes.dto.MovimentacoesDto;
import br.com.api.petpoints.domain.users.gerente.features.movimentacoes.form.FiltroMovimentacoesForm;
import br.com.api.petpoints.shared.dto.ProdutoFiltroDto;
import br.com.api.petpoints.shared.models.MovimentacaoModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.MovimentacaoRepository;
import br.com.api.petpoints.shared.repository.ProdutoRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import br.com.api.petpoints.shared.utils.ColunaRelatorio;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import br.com.api.petpoints.shared.utils.RelatoriosUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MovimentacoesClinicaServiceImpl implements MovimentacoesClinicaService {

    private final UsuarioRepository usuarioRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;
    private final RelatoriosUtils relatoriosUtils;

    @Override
    public List<MovimentacoesDto> listarMovimentacoes() {
        return MovimentacoesDto.convert(movimentacaoRepository.findAll());
    }

    @Override
    public List<ProdutoFiltroDto> listarProdutosFiltro() {
        return ProdutoFiltroDto.convert(this.produtoRepository.findAll());
    }

    @Override
    public List<LancadoPorDto> listarEstoquistasFiltro() {
        return LancadoPorDto.convert(this.usuarioRepository.findAllByPermissao(TipoUsuario.E));
    }

    @Override
    public byte[] gerarRelatorioMovimentacoes(Long idUsuario, FiltroMovimentacoesForm form) {
        List<MovimentacaoModel> movimentacoes = this.movimentacaoRepository.findAll();
        UsuarioModel usuario = this.usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário não encontrado com ID: " + idUsuario));
        if (form.getLancadoPor() != null)
            movimentacoes = movimentacoes.stream().filter(movimentacao -> Objects.equals(movimentacao.getMovimentadoPor().getId(), form.getLancadoPor())).toList();
        if (form.getProduto() != null)
            movimentacoes = movimentacoes.stream().filter(movimentacao -> Objects.equals(movimentacao.getProduto().getId(), form.getProduto())).toList();
        if (!Objects.equals(form.getTipoMovimentacao(), ""))
            movimentacoes = movimentacoes.stream().filter(movimentacao -> Objects.equals(movimentacao.getTipo().toString(), form.getTipoMovimentacao())).toList();
        String titulo = "Relatório de Movimentações Geral";
        String subtitulo = "Movimentações Realizadas por " + usuario.getNome();
        List<ColunaRelatorio> colunas = List.of(
                new ColunaRelatorio("ID", m -> ((MovimentacaoModel) m).getId()),
                new ColunaRelatorio("Tipo", m -> ((MovimentacaoModel) m).getTipo()),
                new ColunaRelatorio("Data Hora", m -> (LocalDateTimeUtils.converterLocalDateTimeParaPtBr(((MovimentacaoModel) m).getMovimentadoEm()))),
                new ColunaRelatorio("Quantidade", m -> (((MovimentacaoModel) m).getQuantidadeMovimentada() + " Unidades")),
                new ColunaRelatorio("Efetuada Por", m -> ((MovimentacaoModel) m).getMovimentadoPor().getNome()),
                new ColunaRelatorio("Produto", m -> ((MovimentacaoModel) m).getProduto().getNome())
        );
        return this.relatoriosUtils.gerarRelatorioGenerico(colunas, movimentacoes, titulo, subtitulo);
    }
}
