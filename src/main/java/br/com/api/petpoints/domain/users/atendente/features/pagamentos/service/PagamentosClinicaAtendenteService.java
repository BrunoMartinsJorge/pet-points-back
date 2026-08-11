package br.com.api.petpoints.domain.users.atendente.features.pagamentos.service;

import br.com.api.petpoints.domain.users.atendente.features.pagamentos.dto.CardsPagamentosClinica;
import br.com.api.petpoints.domain.users.atendente.features.pagamentos.dto.DetalhesPagamentoClinicaDto;
import br.com.api.petpoints.domain.users.atendente.features.pagamentos.dto.PagamentosClinicaDto;
import br.com.api.petpoints.domain.users.atendente.features.pagamentos.forms.IndeferirPagamentoClinicaForm;

import java.util.List;

public interface PagamentosClinicaAtendenteService {
    CardsPagamentosClinica buscarCardsPagamentoClinica(Long idUsuario);
    List<PagamentosClinicaDto> buscarHistoricoPagamentosClinica();
    List<PagamentosClinicaDto> buscarPagamentosPendentesAtrasados();
    void registrarPagamento(Long idUsuario, Long idPagamento);
    DetalhesPagamentoClinicaDto buscarDetalhesPagamento(Long idPagamento);
    DetalhesPagamentoClinicaDto consultarStatusTransacao(Long idPagamento);
    void indeferirPagamento(Long idUsuario, Long idPagamento, IndeferirPagamentoClinicaForm form);
}
