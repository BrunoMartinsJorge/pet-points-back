package br.com.api.petpoints.modules.atendente.features.consultas.service;

import br.com.api.petpoints.modules.atendente.features.consultas.dto.ConsultasDto;
import br.com.api.petpoints.modules.atendente.features.consultas.forms.DeferirIndeferirSolicitacaoConsultaForm;

import java.util.List;

public interface ConsultasAtendenteService {

    void deferirSolicitacaoDeConsulta(DeferirIndeferirSolicitacaoConsultaForm form, Long idUsuario);
    void indeferirSolicitacaoDeConsulta(DeferirIndeferirSolicitacaoConsultaForm form, Long idUsuario);
    List<ConsultasDto> listarConsultasComPagamentosPendentesDoCliente(Long idCliente);
    List<ConsultasDto> listarConsultasPendentes();
}
