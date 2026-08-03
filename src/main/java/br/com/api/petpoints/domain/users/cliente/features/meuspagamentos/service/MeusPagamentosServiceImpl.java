package br.com.api.petpoints.domain.users.cliente.features.meuspagamentos.service;

import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.domain.users.cliente.features.meuspagamentos.dto.CardsPagamentoDto;
import br.com.api.petpoints.domain.users.cliente.features.meuspagamentos.dto.DetalhesPagamentoDto;
import br.com.api.petpoints.domain.users.cliente.features.meuspagamentos.dto.PagamentosDto;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto.MinhasConsultasDto;
import br.com.api.petpoints.domain.users.cliente.shared.dto.PagamentoConsultaDto;
import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.features.payment.dto.MercadoPagoDto;
import br.com.api.petpoints.shared.features.payment.dto.PagamentoDto;
import br.com.api.petpoints.shared.features.payment.service.PagamentoService;
import br.com.api.petpoints.shared.models.*;
import br.com.api.petpoints.shared.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeusPagamentosServiceImpl implements MeusPagamentosService {

    private final ConsultaRepository consultaRepository;
    private final PagamentoRepository pagamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ArquivoRepository arquivoRepository;
    private final ComprovanteRepository comprovanteRepository;
    private final PagamentoService pagamentoService;

    private UsuarioModel getUsuarioPorId(Long idUsuario) {
        return this.usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: " + idUsuario));
    }

    private ConsultaModel getConsultaPorId(Long idConsulta) {
        return this.consultaRepository.findById(idConsulta).orElseThrow(() -> new ObjectNotFoundException("Consulta com ID: " + idConsulta + " não encontrada!"));
    }

    private PagamentoModel getPagamentoPorId(Long idPagamento) {
        return this.pagamentoRepository.findById(idPagamento).orElseThrow(() -> new ObjectNotFoundException("Pagamento com ID: " + idPagamento + " não encontrado!"));
    }

    @Override
    public CardsPagamentoDto buscarInformacoesCards(Long idUsuario) {
        List<ConsultaModel> consultas = this.consultaRepository.findAllBySolicitante_Id(idUsuario);
        int efetuados = consultas.stream().filter(consulta -> consulta.getStatus().equals(StatusConsultaEnum.FINALIZADO) && (consulta.getPagamento() != null && consulta.getPagamento().getStatusPagamento().equals(StatusPagamentoEnum.ENVIADO) || (consulta.getPagamento() != null && consulta.getPagamento().getStatusPagamento().equals(StatusPagamentoEnum.APROVADO)))).toList().size();
        int atrasados = consultas.stream().filter(consulta -> consulta.getStatus().equals(StatusConsultaEnum.FINALIZADO) && consulta.getPagamento() != null && consulta.getPagamento().getDataLimitePagamento() != null && consulta.getPagamento().getDataLimitePagamento().isBefore(LocalDateTime.now())).toList().size();
        int reprovados = consultas.stream().filter(consulta -> consulta.getStatus().equals(StatusConsultaEnum.FINALIZADO) && consulta.getPagamento().getStatusPagamento().equals(StatusPagamentoEnum.REPROVADO)).toList().size();
        return new CardsPagamentoDto(efetuados, atrasados, reprovados);
    }

    @Override
    public List<PagamentosDto> listarPagamentosPendentesAtrasados(Long idUsuario) {
        List<ConsultaModel> consultas = this.consultaRepository.buscarConsultasPorUsuarioComPagamentosPendentes(idUsuario);
        return PagamentosDto.convert(consultas);
    }

    @Override
    public List<PagamentosDto> listarPagamentosReprovados(Long idUsuario) {
        List<ConsultaModel> consultas = this.consultaRepository.findAllByPagamento_StatusPagamentoAndSolicitante_Id(StatusPagamentoEnum.REPROVADO, idUsuario);
        return PagamentosDto.convert(consultas);
    }

    @Override
    public List<PagamentosDto> listarHistoricoPagamentos(Long idUsuario) {
        List<ConsultaModel> consultas = this.consultaRepository.findAllBySolicitante_Id(idUsuario).stream().filter(consulta -> !consulta.getPagamento().getStatusPagamento().equals(StatusPagamentoEnum.REPROVADO) && !consulta.getPagamento().getStatusPagamento().equals(StatusPagamentoEnum.PENDENTE)).toList();
        return PagamentosDto.convert(consultas);
    }

    @Override
    public MinhasConsultasDto buscarInformacoesConsultaPagamento(Long idConsulta) {
        ConsultaModel consulta = this.getConsultaPorId(idConsulta);
        return new MinhasConsultasDto(consulta);
    }
    @Override
    @Transactional
    public void alterarFormaPagamento(Long idPagamento, TipoPagamentoEnum novaForma) {
        PagamentoModel pagamento = this.getPagamentoPorId(idPagamento);
        if (pagamento.getStatusPagamento() == StatusPagamentoEnum.APROVADO)
            throw new RuntimeException("O pagamento já foi aprovado, então não pode mais receber alterações!");
        pagamento.setTipoPagamento(novaForma);
        pagamento.setStatusPagamento(StatusPagamentoEnum.PENDENTE);
        this.pagamentoRepository.save(pagamento);
    }

    @Override
    public PagamentoConsultaDto buscarPagamentoPorId(Long idPagamento) {
        PagamentoModel pagamento = this.getPagamentoPorId(idPagamento);
        if (!pagamento.getTipoPagamento().equals(TipoPagamentoEnum.PIX)) {
            return new PagamentoConsultaDto(pagamento);
        } else {
            try {
                MercadoPagoDto.OrderResponse informacoesPagamento = this.pagamentoService.buscarPagamentoPix(pagamento.getId());
                PagamentoDto.PagamentoPixResponse respostaPix = this.pagamentoService.getPagamentoPixResponse(informacoesPagamento, pagamento);
                return new PagamentoConsultaDto(pagamento, respostaPix);
            } catch (Exception e) {
                log.error("Ocorreu um erro ao buscar os detalhes do pagamento via PIX desse pagamento! {}", idPagamento);
            }
            return null;
        }
    }

    private UUID salvarArquivo(MultipartFile form) {
        if (form.getSize() > 5_000_000) throw new RuntimeException("Arquivo passa de 5MB!");
        List<String> tiposPermitidos = List.of(
                "image/png",
                "image/jpeg",
                "application/pdf"
        );
        if (!tiposPermitidos.contains(form.getContentType()))
            throw new RuntimeException("Tipo inválido");
        ArquivosModel arquivo = new ArquivosModel();
        try {
            arquivo.setConteudo(form.getBytes());
            arquivo.setNome(form.getOriginalFilename());
            arquivo.setTipo(form.getContentType());
            return this.arquivoRepository.save(arquivo).getId();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
