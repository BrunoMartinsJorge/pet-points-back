package br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.service;

import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto.*;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.forms.ReagendamentoConsultaForm;
import br.com.api.petpoints.shared.form.AvaliacaoForm;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.forms.CancelarConsultaForm;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.forms.SolicitacaoConsultaForm;
import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface MinhasConsultasClienteService {

    InformacoesCardsConsultasClienteDto gerarInformacoesCards(Long idUsuario);
    List<MinhasConsultasDto> listarConsultasAprovadas(Long idUsuario);
    List<MinhasConsultasDto> listarMinhasConsultas(Long idUsuario);
    List<MinhasConsultasDto> listarConsultasPendentes(Long idUsuario);
    MinhasConsultasDto buscarProximaConsulta(Long idUsuario);
    MinhasConsultasDto buscarConsultaAtual(Long idUsuario);
    DetalhesConsultaSelecionadaDto buscarDetalhesConsulta(Long idConsulta);
    void solicitarNovaConsulta(Long idUsuario, SolicitacaoConsultaForm form);
    void cancelarConsulta(Long idUsuario, CancelarConsultaForm form);
    List<TiposConsultaDto> listarTiposConsulta();
    List<VeterinariosTipoConsultaDto> listarVeterinariosTipoConsulta(Long idTipoConsulta);
    List<DiaConsultasVeterinarioDto> buscarDiasHorariosDisponiveisVeterinario(Long idVeterinario);
    List<OpcoesPetConsultasDto> buscarPetsConsulta(Long idUsuario);
    PagamentoConsultaDto buscarPagamentoConsulta(Long idConsulta);
    void alterarFormaPagamentoConsulta(Long idUsuario, Long idConsulta, TipoPagamentoEnum formaPagamento);
    AvaliacaoConsultaDto buscarAvaliacaoPorConsulta(Long idUsuario, Long idConsulta);
    void avaliarConsulta(Long idUsuario, Long idConsulta, AvaliacaoForm form);
    MinhasConsultasDto buscarConsultaPorId(Long idConsulta);
    void reagendarConsulta(Long idUsuario, ReagendamentoConsultaForm form);
}
