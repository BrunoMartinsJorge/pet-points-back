package br.com.api.petpoints.modules.atendente.features.consultas.service;

import br.com.api.petpoints.modules.atendente.features.consultas.dto.ConsultasDto;
import br.com.api.petpoints.modules.atendente.features.consultas.forms.DeferirIndeferirSolicitacaoConsultaForm;
import br.com.api.petpoints.modules.veterinario.features.minhasconsultas.dto.ConsultaDto;
import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.enums.TipoLogEnum;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.features.logs.LogsServiceImpl;
import br.com.api.petpoints.shared.models.ConsultaModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import br.com.api.petpoints.shared.repository.PetRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultasAtendenteServiceImpl implements ConsultasAtendenteService {

    private final UsuarioRepository usuarioRepository;
    private final PetRepository petRepository;
    private final ConsultaRepository consultaRepository;
    private final LogsServiceImpl logsService;

    private ConsultaModel getConsultaPorId(Long idConsulta) {
        return consultaRepository.findById(idConsulta).orElseThrow(() -> new ObjectNotFoundException("Consulta com ID: " + idConsulta + " não encontrada!"));
    }

    private UsuarioModel getUsuarioPorId(Long idUsuario) {
        return usuarioRepository.findById(idUsuario).orElseThrow(() -> new ObjectNotFoundException("Usuário com ID: " + idUsuario + " não encontrado!"));
    }

    public void deferirSolicitacaoDeConsulta(DeferirIndeferirSolicitacaoConsultaForm form, Long idUsuario) {
        ConsultaModel consulta = this.getConsultaPorId(form.getIdSolicitacao());
        UsuarioModel atendente = this.getUsuarioPorId(idUsuario);
        if (consulta.getStatus() != StatusConsultaEnum.PENDENTE) throw new RuntimeException("Consulta já deferida!");
        consulta.setStatus(StatusConsultaEnum.APROVADA);
        consulta.setAtendente(atendente);
        consulta.setDeferidoEm(LocalDateTime.now());
        this.logsService.registrarLog(atendente, TipoLogEnum.DEFERIU_CONSULTA);
        consultaRepository.save(consulta);
    }

    public void indeferirSolicitacaoDeConsulta(DeferirIndeferirSolicitacaoConsultaForm form, Long idUsuario) {
        ConsultaModel consulta = this.getConsultaPorId(form.getIdSolicitacao());
        UsuarioModel atendente = this.getUsuarioPorId(idUsuario);
        if (consulta.getStatus() != StatusConsultaEnum.PENDENTE) throw new RuntimeException("Consulta já deferida!");
        consulta.setStatus(StatusConsultaEnum.REPROVADA);
        consulta.setAtendente(atendente);
        consulta.setDeferidoEm(LocalDateTime.now());
        consulta.setMotivoIndeferimento(form.getMotivoIndeferimento());
        this.logsService.registrarLog(atendente, TipoLogEnum.INDEFERIU_CONSULTA);
        consultaRepository.save(consulta);
    }

    public List<ConsultasDto> listarConsultasComPagamentosPendentesDoCliente(Long idCliente) {
        return this.consultaRepository.findAllBySolicitante_IdAndPagamentoIsNull(idCliente).stream().filter(consulta -> consulta.getStatus() == StatusConsultaEnum.FINALIZADO).map(ConsultasDto::new).toList();
    }

    @Override
    public List<ConsultasDto> listarConsultasPendentes() {
        List<ConsultaModel> consultas = this.consultaRepository.findAllByStatus(StatusConsultaEnum.PENDENTE);
        return consultas.stream().map(ConsultasDto::new).toList();
    }
}
