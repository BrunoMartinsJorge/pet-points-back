package br.com.api.petpoints.modules.users.estoquista.features.dashboard.service;

import br.com.api.petpoints.modules.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.modules.users.estoquista.features.dashboard.dto.HistoricoMovimentacoesMensalDto;
import br.com.api.petpoints.modules.users.estoquista.shared.dto.ProdutoEstoqueDto;
import br.com.api.petpoints.shared.models.MovimentacaoModel;
import br.com.api.petpoints.shared.models.ProdutoModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.MovimentacaoRepository;
import br.com.api.petpoints.shared.repository.ProdutoRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardEstoquistaServiceImpl implements DashboardEstoquistaService {

    private final UsuarioRepository usuarioRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;

    private UsuarioModel getUsuarioPorId(Long id) {
        return this.usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: " + id + " não encontrado!"));
    }

    @Override
    public List<HistoricoMovimentacoesMensalDto> listarMovimentacoesMensaisGrafico(Long idUsuario) {
        LocalDate dataAtual = LocalDate.now();
        List<MovimentacaoModel> movimentacoes = this.movimentacaoRepository.findAllByMovimentadoPor_Id(idUsuario).stream().filter(movimentacao -> movimentacao.getMovimentadoEm().getMonthValue() == dataAtual.getMonthValue() && movimentacao.getMovimentadoEm().getYear() == dataAtual.getYear()
        ).toList();
        return HistoricoMovimentacoesMensalDto.convert(movimentacoes);
    }

    @Override
    public List<ProdutoEstoqueDto> listarProdutosAbaixoEstoque() {
        return ProdutoEstoqueDto.convert(this.produtoRepository.listarProdutosAbaixoDeEstoque());
    }
}
