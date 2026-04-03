package br.com.api.petpoints.modules.cliente.features.minhasconsultas.service;

import br.com.api.petpoints.modules.cliente.features.minhasconsultas.dto.MinhasConsultasDto;
import br.com.api.petpoints.modules.cliente.features.minhasconsultas.forms.CancelarConsultaForm;
import br.com.api.petpoints.modules.cliente.features.minhasconsultas.forms.SolicitacaoConsultaForm;
import br.com.api.petpoints.modules.usuario.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.enums.StatusPerfilEnum;
import br.com.api.petpoints.shared.enums.TipoLogEnum;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.exception.custom.PerfilDesativadoException;
import br.com.api.petpoints.shared.features.logs.LogsServiceImpl;
import br.com.api.petpoints.shared.models.ConsultaModel;
import br.com.api.petpoints.shared.models.PetModel;
import br.com.api.petpoints.shared.models.TipoConsultaModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import br.com.api.petpoints.shared.repository.PetRepository;
import br.com.api.petpoints.shared.repository.TipoConsultaRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MinhasConsultasClienteServiceImpl implements MinhasConsultasClienteService {

    private final UsuarioRepository usuarioRepository;
    private final ConsultaRepository consultaRepository;
    private final PetRepository petRepository;
    private final TipoConsultaRepository tipoConsultaRepository;
    private final LogsServiceImpl logsService;

    @Override
    public List<MinhasConsultasDto> listarMinhasConsultas(Long idUsuario) {
        Optional<UsuarioModel> usuario = usuarioRepository.findById(idUsuario);
        if (usuario.isEmpty()) throw new UsuarioNaoEncontrado("Usuário com ID " + idUsuario + " não encontrado!");
        if (usuario.get().getStatusPerfilEnum().equals(StatusPerfilEnum.D))
            throw new PerfilDesativadoException("Perfil com email " + usuario.get().getEmail() + " desabilitado!");
        List<ConsultaModel> minhasConsultas = this.consultaRepository.findAllBySolicitante_Id(idUsuario);
        return minhasConsultas.stream().map(MinhasConsultasDto::new).toList();
    }

    private UsuarioModel getUsuarioPorId(Long idUsuario) {
        return this.usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: " + idUsuario));
    }

    private PetModel getPetPorId(Long idPet) {
        return this.petRepository.findById(idPet).orElseThrow(() -> new UsuarioNaoEncontrado("Consulta com ID: " + idPet + " não encontrado!"));
    }

    private ConsultaModel getConsultaPorId(Long idConsulta) {
        return this.consultaRepository.findById(idConsulta).orElseThrow(() -> new UsuarioNaoEncontrado("Consulta com ID: " + idConsulta + " não encontrada!"));
    }

    @Override
    @Transactional
    public MinhasConsultasDto solicitarNovaConsulta(Long idUsuario, SolicitacaoConsultaForm form) {
        this.validarSolicitacaoDeConsulta(idUsuario, form);
        PetModel pet = this.getPetPorId(form.getIdPet());
        UsuarioModel solicitante = this.getUsuarioPorId(idUsuario);
        if (pet.getTutor() != solicitante) throw new RuntimeException("Esse Pet não é seu!");
        UsuarioModel veterinario = this.getUsuarioPorId(form.getIdVeterinario());
        TipoConsultaModel tipoConsulta = this.tipoConsultaRepository.findById(form.getIdTipoConsulta()).orElseThrow(() -> new ObjectNotFoundException("Tipo de consulta com ID: " + form.getIdTipoConsulta() + " não encontrado!"));
        ConsultaModel consulta = new ConsultaModel();
        consulta.setSolicitante(solicitante);
        consulta.setDataConsulta(form.getDataConsulta());
        consulta.setObservacoes(form.getObservacoes());
        consulta.setVeterinario(veterinario);
        consulta.setTipoConsulta(tipoConsulta);
        consulta.setPet(pet);
        consulta = this.consultaRepository.save(consulta);
        return new MinhasConsultasDto(consulta);
    }

    @Override
    public void cancelarConsulta(Long idUsuario, CancelarConsultaForm form) {
        UsuarioModel cliente = this.getUsuarioPorId(idUsuario);
        ConsultaModel consulta = this.getConsultaPorId(form.getIdConsulta());
        if (consulta.getStatus() == StatusConsultaEnum.INICIADO || consulta.getStatus() == StatusConsultaEnum.FINALIZADO || consulta.getStatus() == StatusConsultaEnum.REPROVADA)
            throw new RuntimeException("A solicita");
        consulta.setStatus(StatusConsultaEnum.CANCELADO);
        consulta.setMotivoCancelamento(form.getMotivoCancelamento());
        consulta.setCanceladoEm(LocalDateTime.now());
        this.consultaRepository.save(consulta);
        this.logsService.registrarLog(cliente, TipoLogEnum.CANCELOU_CONSULTA);
    }

    private void validarSolicitacaoDeConsulta(Long idUsuario, SolicitacaoConsultaForm form) {
        List<ConsultaModel> consultasPagamentosPendentes = this.consultaRepository.findAllBySolicitante_IdAndPagamentoIsNull(idUsuario).stream().filter(consulta -> consulta.getStatus() != StatusConsultaEnum.PENDENTE).toList();
        // if (!consultasPagamentosPendentes.isEmpty()) throw new RuntimeException("Você ainda tem pagamentos pendentes!");
        LocalDateTime dataSolicitada = form.getDataConsulta();
        List<ConsultaModel> consultasDoVeterinario =
                this.consultaRepository
                        .findAllByVeterinario_Id(form.getIdVeterinario())
                        .stream()
                        .filter(consulta -> consulta.getDataConsulta().toLocalDate().equals(dataSolicitada.toLocalDate()) && consulta.getDataConsulta().isAfter(dataSolicitada.minusHours(1)) && consulta.getDataConsulta().isBefore(dataSolicitada.plusHours(1))
                        )
                        .toList();
        if (!consultasDoVeterinario.isEmpty()) throw new RuntimeException("Já existe uma consulta nesse periodo!");
    }
}
