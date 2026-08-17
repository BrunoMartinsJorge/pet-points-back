package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.service;

import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto.ConsultaAtualDto;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto.ConsultaVeterinarioDto;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto.InformacoesConsultaSelecionadaDto;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto.ProdutoCobrancaDto;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.forms.FinalizarConsultaForm;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.forms.ItemCobrancaForm;
import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.enums.TipoLogEnum;
import br.com.api.petpoints.shared.enums.TipoMovimentacaoEnum;
import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import br.com.api.petpoints.shared.enums.TiposNotificacoesEnum;
import br.com.api.petpoints.shared.exception.custom.IllegalAccessException;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.features.logs.LogsServiceImpl;
import br.com.api.petpoints.shared.features.notificacoes.controller.NotificacoesController;
import br.com.api.petpoints.shared.features.notificacoes.form.NovaNotificacaoForm;
import br.com.api.petpoints.shared.features.payment.dto.PagamentoDto;
import br.com.api.petpoints.shared.features.payment.service.PagamentoService;
import br.com.api.petpoints.shared.models.ConsultaModel;
import br.com.api.petpoints.shared.models.ItemConsultaModel;
import br.com.api.petpoints.shared.models.MovimentacaoModel;
import br.com.api.petpoints.shared.models.PagamentoModel;
import br.com.api.petpoints.shared.models.ProdutoModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.*;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinhasConsultaVeterinarioServiceImpl implements MinhasConsultaVeterinarioService {

    private final UsuarioRepository usuarioRepository;
    private final ConsultaRepository consultaRepository;
    private final ProdutoRepository produtoRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final LogsServiceImpl logsService;
    private final NotificacoesController notificacoesController;
    private final PagamentoService pagamentoService;
    private final PagamentoRepository pagamentoRepository;

    @Override
    public List<ConsultaVeterinarioDto> listarMinhasConsultas(Long idUsuario) {
        List<ConsultaModel> minhasConsultas = this.consultaRepository.findAllByVeterinario_Id(idUsuario);
        return ConsultaVeterinarioDto.convert(minhasConsultas);
    }

    @Override
    public List<ConsultaVeterinarioDto> listarMinhasConsultasDoDia(Long idUsuario) {
        List<ConsultaModel> minhasConsultas = this.consultaRepository.findAllByVeterinario_Id(idUsuario).stream()
                .filter(consulta -> consulta.getDataConsulta().toLocalDate().equals(LocalDate.now()) && consulta.getStatus().equals(StatusConsultaEnum.APROVADA)).toList();
        return ConsultaVeterinarioDto.convert(minhasConsultas);
    }

    @Override
    public ConsultaAtualDto buscarConsultaAtualVeterinario(Long idUsuario) {
        List<ConsultaModel> consultasDoDia = this.consultaRepository.findAllByVeterinario_Id(idUsuario).stream()
                .filter(consulta -> consulta.getStatus().equals(StatusConsultaEnum.INICIADO)).toList();
        if (consultasDoDia.isEmpty())
            return new ConsultaAtualDto();
        if (consultasDoDia.size() > 1)
            throw new RuntimeException("Duas consultas não podem estar iniciadas ao mesmo tempo!");
        return new ConsultaAtualDto(consultasDoDia.getFirst());
    }

    @Override
    public InformacoesConsultaSelecionadaDto buscarInformacoesConsulta(Long idConsulta, Long idUsuario) {
        ConsultaModel consulta = this.getConsultaPorId(idConsulta);
        if (!consulta.getVeterinario().getId().equals(idUsuario))
            throw new IllegalArgumentException("Você não é o veterinário responsável por esta consulta!");
        return new InformacoesConsultaSelecionadaDto(consulta);
    }

    private ConsultaModel getConsultaPorId(Long idConsulta) {
        return this.consultaRepository.findById(idConsulta).orElseThrow(() -> new ObjectNotFoundException("Consulta com ID: " + idConsulta + " não encontrada!"));
    }

    private UsuarioModel getUsuarioPorId(Long idUsuario) {
        return this.usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: " + idUsuario + " não encontrado!"));
    }

    @Override
    @Transactional
    public void iniciarConsulta(Long idUsuario, Long idConsulta) {
        ConsultaModel consulta = this.getConsultaPorId(idConsulta);
        if (consulta.getStatus() != StatusConsultaEnum.APROVADA)
            throw new RuntimeException("A consulta não pode ser iniciada com o estado: " + consulta.getStatus().getDescricao() + "!");
        consulta.setStatus(StatusConsultaEnum.INICIADO);
        consulta.setIniciadoEm(LocalDateTime.now());
        consulta = this.consultaRepository.save(consulta);
        this.logsService.registrarLog(this.getUsuarioPorId(idUsuario), TipoLogEnum.CONSULTA_INICIADA);
        this.enviarNotificacaoCliente(consulta);
    }

    @Override
    public List<ProdutoCobrancaDto> listarProdutosParaCobranca() {
        List<ProdutoModel> disponiveis = this.produtoRepository.findAll().stream()
                .filter(produto -> produto.getQuantidadeEstoque() > 0)
                .toList();
        return ProdutoCobrancaDto.convert(disponiveis);
    }

    @Override
    @Transactional
    public void finalizarConsulta(Long idUsuario, Long idConsulta, FinalizarConsultaForm form) {
        ConsultaModel consulta = this.getConsultaPorId(idConsulta);
        if (consulta.getStatus() != StatusConsultaEnum.INICIADO)
            throw new RuntimeException("A consulta não pode ser finalizada com o estado: " + consulta.getStatus().getDescricao() + "!");
        if (consulta.getPagamento() != null)
            throw new RuntimeException("Esta consulta já possui uma cobrança gerada!");
        if (form == null || form.getResumo() == null || form.getResumo().isBlank())
            throw new IllegalArgumentException("O resumo da consulta é obrigatório para finalizá-la!");

        UsuarioModel veterinario = this.getUsuarioPorId(idUsuario);

        consulta.setStatus(StatusConsultaEnum.FINALIZADO);
        consulta.setFinalizadoEm(LocalDateTime.now());
        consulta.setResumoConsulta(form.getResumo());
        this.registrarItensCobranca(consulta, form.getItens(), veterinario);
        double valorEmProdutos = this.gerarValorAdicionalProdutosUsados(form.getItens());
        double valorConsulta = consulta.getTipoConsulta().getValor() + valorEmProdutos;
        consulta.setPagamento(this.gerarCobrancaDaConsulta(consulta, valorConsulta));

        consulta = this.consultaRepository.save(consulta);
        log.info("Consulta finalizada com sucesso: ID {} - {}", idConsulta, LocalDateTime.now());
        this.logsService.registrarLog(veterinario, TipoLogEnum.CONSULTA_FINALIZADA);
        this.enviarNotificacaoCliente(consulta);
        log.info("Notificações enviadas para cliente!");
    }

    /**
     * Lança os produtos consumidos durante a consulta (vacinas, medicamentos, etc.),
     * dando baixa no estoque e registrando uma movimentação de saída em nome do
     * veterinário para cada produto — do mesmo modo que o estoquista faria manualmente.
     * <p>
     * Itens repetidos do mesmo produto são somados antes da validação, de forma que
     * a quantidade total lançada nunca ultrapasse o estoque disponível.
     */
    private void registrarItensCobranca(ConsultaModel consulta, List<ItemCobrancaForm> itens, UsuarioModel veterinario) {
        consulta.getItensCobranca().clear();
        if (itens == null || itens.isEmpty())
            return;

        Map<Long, Integer> quantidadePorProduto = new LinkedHashMap<>();
        for (ItemCobrancaForm item : itens) {
            if (item.getIdProduto() == null)
                throw new IllegalArgumentException("Informe o produto de todos os itens de cobrança!");
            if (item.getQuantidade() <= 0)
                throw new IllegalArgumentException("A quantidade dos itens de cobrança deve ser maior que zero!");
            quantidadePorProduto.merge(item.getIdProduto(), item.getQuantidade(), Integer::sum);
        }

        quantidadePorProduto.forEach((idProduto, quantidade) -> {
            ProdutoModel produto = this.produtoRepository.findById(idProduto)
                    .orElseThrow(() -> new ObjectNotFoundException("Produto com ID: " + idProduto + " não encontrado!"));
            if (produto.getQuantidadeEstoque() < quantidade)
                throw new IllegalAccessException("O produto " + produto.getNome() + " possui apenas " + produto.getQuantidadeEstoque() + " unidade(s) em estoque!");

            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
            this.produtoRepository.save(produto);
            this.movimentacaoRepository.save(this.gerarMovimentacaoSaida(produto, veterinario, quantidade));
            consulta.getItensCobranca().add(new ItemConsultaModel(consulta, produto, quantidade));

            log.info("Baixa de estoque na consulta {}: {} x {} unidade(s).", consulta.getId(), produto.getNome(), quantidade);
        });

        this.logsService.registrarLog(veterinario, TipoLogEnum.MOVIMENTACAO_SAIDA);
    }

    private MovimentacaoModel gerarMovimentacaoSaida(ProdutoModel produto, UsuarioModel veterinario, int quantidade) {
        MovimentacaoModel movimentacao = new MovimentacaoModel();
        movimentacao.setTipo(TipoMovimentacaoEnum.SAIDA);
        movimentacao.setProduto(produto);
        movimentacao.setMovimentadoPor(veterinario);
        movimentacao.setQuantidadeMovimentada(quantidade);
        return movimentacao;
    }

    /**
     * Gera a cobrança referente à consulta de acordo com a forma de pagamento
     * escolhida pelo cliente no momento da solicitação. Caso o cliente não
     * tenha escolhido (fluxo legado / dado ausente), assume-se dinheiro
     * (pagamento presencial), já que somente o PIX possui integração online.
     * <p>
     * O valor cobrado é o do tipo de consulta somado aos itens lançados pelo
     * veterinário na finalização.
     */
    private PagamentoModel gerarCobrancaDaConsulta(ConsultaModel consulta, double valorConsulta) {
        TipoPagamentoEnum formaPagamento = consulta.getFormaPagamento() != null
                ? consulta.getFormaPagamento()
                : TipoPagamentoEnum.DINHEIRO;
        PagamentoModel pagamento = new PagamentoModel();
        BigDecimal valor = consulta.valorTotalCobranca();
        switch (formaPagamento) {
            case PIX -> {
                return this.gerarPagamentoPix(consulta, pagamento, BigDecimal.valueOf(valorConsulta));
            }
            case CARTAO -> {
                return this.gerarPagamentoCartao(consulta, BigDecimal.valueOf(valorConsulta));
            }
            case DINHEIRO -> {
                return this.pagamentoService.criarPagamentoPresencial(pagamento, formaPagamento, valor, consulta.getSolicitante());
            }
            default -> throw new IllegalArgumentException("Tipo de Pagamento não computado! " + formaPagamento);
        }
    }

    protected PagamentoModel gerarPagamentoCartao(ConsultaModel consulta, BigDecimal valor) {
        PagamentoModel pagamento = new PagamentoModel();
        pagamento.setValorPagamento(valor);
        pagamento.setDataLimitePagamento(
                LocalDateTime.now()
                        .plusWeeks(1)
                        .with(LocalTime.MAX)
        );
        pagamento.setEmitidoPor(consulta.getSolicitante());
        pagamento.setTipoPagamento(TipoPagamentoEnum.CARTAO);
        return this.pagamentoRepository.save(pagamento);
    }

    private PagamentoModel gerarPagamentoPix(ConsultaModel consulta, PagamentoModel pagamento, BigDecimal valor) {
        PagamentoDto.CriarPagamentoPixForm formPagamento = new PagamentoDto.CriarPagamentoPixForm(
                valor,
                "Pagamento referente à consulta na clínica Pet Points do cliente " + consulta.getSolicitante().getNome(),
                consulta.getSolicitante().getEmail(),
                consulta.getSolicitante().getNome(),
                "CONSULTA_ID_" + consulta.getId(),
                consulta.getSolicitante().getCpf());
        this.pagamentoService.gerarCobrancaPix(pagamento, formPagamento, consulta.getSolicitante());
        return pagamento;
    }

    private void enviarNotificacaoCliente(ConsultaModel consulta) {
        if (!consulta.getStatus().equals(StatusConsultaEnum.FINALIZADO) && !consulta.getStatus().equals(StatusConsultaEnum.INICIADO))
            return;
        String status = consulta.getStatus().equals(StatusConsultaEnum.INICIADO)
                ? "iniciada"
                : "finalizada";

        String complemento = consulta.getStatus().equals(StatusConsultaEnum.INICIADO)
                ? "Acompanhe o andamento da consulta pelo sistema."
                : "Caso não esteja na clínica, compareça para retirar seu pet.";

        String conteudo = String.format(
                "Olá, %s! Sua consulta com o(a) Dr(a). %s foi %s às %s. %s Obrigado por utilizar a Pet Points.",
                consulta.getSolicitante().getNome(),
                consulta.getVeterinario().getNome(),
                status,
                LocalDateTimeUtils.converterLocalDateTimeParaPtBr(
                        consulta.getStatus().equals(StatusConsultaEnum.INICIADO)
                                ? consulta.getIniciadoEm()
                                : consulta.getFinalizadoEm()
                ),
                complemento
        );

        if (conteudo.length() > 250) {
            conteudo = conteudo.substring(0, 250);
        }

        NovaNotificacaoForm form = new NovaNotificacaoForm(
                consulta.getSolicitante().getId(),
                consulta.getStatus().equals(StatusConsultaEnum.INICIADO)
                        ? "Consulta Iniciada!"
                        : "Consulta Finalizada!",
                conteudo,
                TiposNotificacoesEnum.CONSULTA
        );
        this.notificacoesController.enviarNotificacao(form);
    }

    private double gerarValorAdicionalProdutosUsados(List<ItemCobrancaForm> itens) {
        List<Double> produtos = this.produtoRepository.findAllByIdIn(itens.stream().map(ItemCobrancaForm::getIdProduto).toList()).stream().map(ProdutoModel::getValorUnitario).toList();
        if (produtos.isEmpty()) return 0;
        return produtos.stream().reduce(Double::sum).orElse(0.0);
    }

    @Override
    public Object gerarPrescricao(Long idUsuario, Long idConsulta) {
        return null;
    }
}
