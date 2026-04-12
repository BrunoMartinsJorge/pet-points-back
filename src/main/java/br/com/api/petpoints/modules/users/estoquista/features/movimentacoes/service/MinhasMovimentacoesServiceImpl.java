package br.com.api.petpoints.modules.users.estoquista.features.movimentacoes.service;

import br.com.api.petpoints.modules.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.modules.users.estoquista.features.movimentacoes.dto.MinhasMovimentacoesDto;
import br.com.api.petpoints.modules.users.estoquista.features.movimentacoes.form.RelatorioMovimentacoesForm;
import br.com.api.petpoints.shared.models.MovimentacaoModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.MovimentacaoRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import br.com.api.petpoints.shared.utils.ColunaRelatorio;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import br.com.api.petpoints.shared.utils.RelatoriosUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MinhasMovimentacoesServiceImpl implements MinhasMovimentacoesService {

    private final UsuarioRepository usuarioRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final RelatoriosUtils relatoriosUtils;

    @Override
    public List<MinhasMovimentacoesDto> listarMovimentacoesEstoquista(Long idUsuario) {
        return MinhasMovimentacoesDto.convert(this.movimentacaoRepository.findAllByMovimentadoPor_Id(idUsuario));
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
                new ColunaRelatorio("Quantidade", m -> ((MovimentacaoModel) m).getQuantidadeMovimentada()),
                new ColunaRelatorio("Produto", m -> ((MovimentacaoModel) m).getProduto().getNome())
        );
        return this.relatoriosUtils.gerarRelatorioGenerico(colunas, movimentacoes, titulo, subtitulo);
    }
}