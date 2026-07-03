package br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.service;

import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto.*;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.forms.AvaliacaoConsultaForm;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.forms.CancelarConsultaForm;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.forms.SolicitacaoConsultaForm;
import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MinhasConsultasClienteService {

    List<MinhasConsultasDto> listarConsultasAprovadas(Long idUsuario);
    List<MinhasConsultasDto> listarMinhasConsultas(Long idUsuario);
    List<MinhasConsultasDto> listarConsultasPendentes(Long idUsuario);
    DetalhesConsultaSelecionadaDto buscarDetalhesConsulta(Long idConsulta);
    void solicitarNovaConsulta(Long idUsuario, SolicitacaoConsultaForm form);
    void cancelarConsulta(Long idUsuario, CancelarConsultaForm form);
    List<TiposConsultaDto> listarTiposConsulta();
    List<VeterinariosTipoConsultaDto> listarVeterinariosTipoConsulta(Long idTipoConsulta);
    List<DiaConsultasVeterinarioDto> buscarDiasHorariosDisponiveisVeterinario(Long idVeterinario);
    List<OpcoesPetConsultasDto> buscarPetsConsulta(Long idUsuario);
    PagamentoDto buscarPagamentoConsulta(Long idConsulta);
    void registrarComprovante(Long idConsulta, Long idUsuario, MultipartFile comprovante);
    void alterarFormaPagamentoConsulta(Long idConsulta, TipoPagamentoEnum formaPagamento);
    AvaliacaoConsultaDto buscarAvaliacaoPorConsulta(Long idUsuario, Long idConsulta);
    void avaliarConsulta(Long idUsuario, Long idConsulta, AvaliacaoConsultaForm form);
    MinhasConsultasDto buscarConsultaPorId(Long idConsulta);
}
