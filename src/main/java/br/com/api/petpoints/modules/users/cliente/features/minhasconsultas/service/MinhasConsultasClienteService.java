package br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.service;

import br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.dto.*;
import br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.forms.CancelarConsultaForm;
import br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.forms.SolicitacaoConsultaForm;

import java.util.List;

public interface MinhasConsultasClienteService {

    List<?> listarConsultasPendentes(Long idUsuario);
    List<MinhasConsultasDto> listarMinhasConsultas(Long idUsuario);
    MinhasConsultasDto solicitarNovaConsulta(Long idUsuario, SolicitacaoConsultaForm form);
    void cancelarConsulta(Long idUsuario, CancelarConsultaForm form);
    List<TiposConsultaDto> listarTiposConsulta();
    List<VeterinariosTipoConsultaDto> listarVeterinariosTipoConsulta(Long idTipoConsulta);
    List<DiaConsultasVeterinarioDto> buscarDiasHorariosDisponiveisVeterinario(Long idVeterinario);
    List<OpcoesPetConsultasDto> buscarPetsConsulta(Long idUsuario);
}
