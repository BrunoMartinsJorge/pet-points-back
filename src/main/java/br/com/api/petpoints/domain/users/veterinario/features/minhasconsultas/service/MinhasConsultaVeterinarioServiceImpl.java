package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.service;

import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto.ConsultaDto;
import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.enums.TipoLogEnum;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.features.logs.LogsServiceImpl;
import br.com.api.petpoints.shared.models.ConsultaModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MinhasConsultaVeterinarioServiceImpl implements MinhasConsultaVeterinarioService {

    private final UsuarioRepository usuarioRepository;
    private final ConsultaRepository consultaRepository;
    private final LogsServiceImpl logsService;

    @Override
    public List<ConsultaDto> listarMinhasConsultas(Long idUsuario) {
        List<ConsultaModel> minhasConsultas = this.consultaRepository.findAllByVeterinario_Id(idUsuario);
        return ConsultaDto.convert(minhasConsultas);
    }

    @Override
    public List<ConsultaDto> listarMinhasConsultasDoDia(Long idUsuario) {
        List<ConsultaModel> minhasConsultas = this.consultaRepository.findAllByVeterinario_Id(idUsuario).stream().filter(consulta -> consulta.getDataConsulta().toLocalDate().equals(LocalDate.now())).toList();
        return ConsultaDto.convert(minhasConsultas);
    }

    private ConsultaModel getConsultaPorId(Long idConsulta) {
        return this.consultaRepository.findById(idConsulta).orElseThrow(() -> new ObjectNotFoundException("Consulta com ID: " + idConsulta + " não encontrada!"));
    }

    private UsuarioModel getUsuarioPorId(Long idUsuario) {
        return this.usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: " + idUsuario + " não encontrado!"));
    }

    @Override
    public void iniciarConsulta(Long idUsuario, Long idConsulta) {
        ConsultaModel consulta = this.getConsultaPorId(idConsulta);
        if (consulta.getStatus() != StatusConsultaEnum.APROVADA)
            throw new RuntimeException("A consulta não pode ser iniciada com o estado: " + consulta.getStatus().getDescricao() + "!");
        consulta.setStatus(StatusConsultaEnum.INICIADO);
        consulta.setIniciadoEm(LocalDateTime.now());
        this.consultaRepository.save(consulta);
        this.logsService.registrarLog(this.getUsuarioPorId(idUsuario), TipoLogEnum.CONSULTA_INICIADA);
    }

    @Override
    public void finalizarConsulta(Long idUsuario, Long idConsulta, String resumo) {
        ConsultaModel consulta = this.getConsultaPorId(idConsulta);
        if (consulta.getStatus() != StatusConsultaEnum.INICIADO)
            throw new RuntimeException("A consulta não pode ser finalizada com o estado: " + consulta.getStatus().getDescricao() + "!");
        consulta.setStatus(StatusConsultaEnum.FINALIZADO);
        consulta.setFinalizadoEm(LocalDateTime.now());
        consulta.setResumoConsulta(resumo);
        this.consultaRepository.save(consulta);
        this.logsService.registrarLog(this.getUsuarioPorId(idUsuario), TipoLogEnum.CONSULTA_FINALIZADA);
    }

    @Override
    public Object gerarPrescricao(Long idUsuario, Long idConsulta) {
        return null;
    }
}
