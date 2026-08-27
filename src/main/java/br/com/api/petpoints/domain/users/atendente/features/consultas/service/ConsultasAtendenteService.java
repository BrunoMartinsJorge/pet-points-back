package br.com.api.petpoints.domain.users.atendente.features.consultas.service;

import br.com.api.petpoints.domain.users.atendente.features.consultas.dto.AvaliacaoConsultaDto;
import br.com.api.petpoints.domain.users.atendente.features.consultas.dto.ConsultasAtendenteDto;
import br.com.api.petpoints.domain.users.atendente.features.consultas.dto.InformacoesPagamentoDto;
import br.com.api.petpoints.domain.users.atendente.features.consultas.dto.OpcaoClienteConsultaDto;
import br.com.api.petpoints.domain.users.atendente.features.consultas.dto.PendenciasFinanceirasClienteDto;
import br.com.api.petpoints.domain.users.atendente.features.consultas.forms.IndeferirConsultaForm;
import br.com.api.petpoints.domain.users.atendente.features.consultas.forms.RegistroConsultaAtendenteForm;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto.DiaConsultasVeterinarioDto;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto.OpcoesPetConsultasDto;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto.TiposConsultaDto;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto.VeterinariosTipoConsultaDto;

import java.util.List;

public interface ConsultasAtendenteService {

    List<ConsultasAtendenteDto> listarConsultasPendentes();
    List<ConsultasAtendenteDto> listarHistoricoDeConsultas();
    void deferirSolicitacaoDeConsulta(Long idConsulta, Long idUsuario);
    void indeferirSolicitacaoDeConsulta(IndeferirConsultaForm form, Long idUsuario);
    List<ConsultasAtendenteDto> listarConsultasComPagamentosPendentesDoCliente(Long idCliente);
    PendenciasFinanceirasClienteDto buscarPendenciasFinanceirasDoCliente(Long idCliente);
    InformacoesPagamentoDto buscarInformacoesPagamento(Long idConsulta);
    AvaliacaoConsultaDto buscarAvaliacao(Long idConsulta);
    ConsultasAtendenteDto buscarConsultaPorId(Long idUsuario, Long idConsulta);

    List<OpcaoClienteConsultaDto> listarClientesParaRegistro();
    List<OpcoesPetConsultasDto> listarPetsDoCliente(Long idCliente);
    List<TiposConsultaDto> listarTiposConsultaParaRegistro();
    List<VeterinariosTipoConsultaDto> listarVeterinariosTipoConsulta(Long idTipoConsulta);
    List<DiaConsultasVeterinarioDto> buscarHorariosVeterinario(Long idVeterinario);
    void registrarConsulta(RegistroConsultaAtendenteForm form, Long idAtendente);
}
