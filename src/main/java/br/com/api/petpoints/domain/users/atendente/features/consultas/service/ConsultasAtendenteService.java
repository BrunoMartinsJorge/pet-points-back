package br.com.api.petpoints.domain.users.atendente.features.consultas.service;

import br.com.api.petpoints.domain.users.atendente.features.consultas.dto.AvaliacaoConsultaDto;
import br.com.api.petpoints.domain.users.atendente.features.consultas.dto.ConsultasAtendenteDto;
import br.com.api.petpoints.domain.users.atendente.features.consultas.dto.InformacoesPagamentoDto;
import br.com.api.petpoints.domain.users.atendente.features.consultas.forms.IndeferirConsultaForm;
import br.com.api.petpoints.domain.users.atendente.features.consultas.forms.IndeferirPagamentoForm;

import java.util.List;

public interface ConsultasAtendenteService {

    List<ConsultasAtendenteDto> listarConsultasPendentes();
    List<ConsultasAtendenteDto> listarHistoricoDeConsultas();
    void deferirSolicitacaoDeConsulta(Long idConsulta, Long idUsuario);
    void indeferirSolicitacaoDeConsulta(IndeferirConsultaForm form, Long idUsuario);
    List<ConsultasAtendenteDto> listarConsultasComPagamentosPendentesDoCliente(Long idCliente);
    InformacoesPagamentoDto buscarInformacoesPagamento(Long idConsulta);
    AvaliacaoConsultaDto buscarAvaliacao(Long idConsulta);
    void avaliarPagamento(Long idConsulta, IndeferirPagamentoForm form);
    ConsultasAtendenteDto buscarConsultaPorId(Long idUsuario, Long idConsulta);
}
