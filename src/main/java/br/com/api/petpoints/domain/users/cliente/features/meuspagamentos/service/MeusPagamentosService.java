package br.com.api.petpoints.domain.users.cliente.features.meuspagamentos.service;

import br.com.api.petpoints.domain.users.cliente.features.meuspagamentos.dto.CardsPagamentoDto;
import br.com.api.petpoints.domain.users.cliente.features.meuspagamentos.dto.DetalhesPagamentoDto;
import br.com.api.petpoints.domain.users.cliente.features.meuspagamentos.dto.PagamentosDto;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto.MinhasConsultasDto;
import br.com.api.petpoints.domain.users.cliente.shared.dto.PagamentoConsultaDto;
import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MeusPagamentosService {
    CardsPagamentoDto buscarInformacoesCards(Long idUsuario);
    List<PagamentosDto> listarPagamentosPendentesAtrasados(Long idUsuario);
    List<PagamentosDto> listarPagamentosReprovados(Long idUsuario);
    List<PagamentosDto> listarHistoricoPagamentos(Long idUsuario);
    MinhasConsultasDto buscarInformacoesConsultaPagamento(Long idConsulta);
    // void registrarNovoComprovante(Long idPagamento, MultipartFile comprovante);
    void alterarFormaPagamento(Long idPagamento, TipoPagamentoEnum novaForma);
    PagamentoConsultaDto buscarPagamentoPorId(Long idPagamento);
}
