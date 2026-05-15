package br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.service;

import br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.dto.*;
import br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.forms.CancelarConsultaForm;
import br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.forms.SolicitacaoConsultaForm;
import br.com.api.petpoints.modules.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.enums.StatusPerfilEnum;
import br.com.api.petpoints.shared.enums.TipoLogEnum;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.exception.custom.PerfilDesativadoException;
import br.com.api.petpoints.shared.features.logs.LogsServiceImpl;
import br.com.api.petpoints.shared.models.*;
import br.com.api.petpoints.shared.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MinhasConsultasClienteServiceImpl implements MinhasConsultasClienteService {

    private final UsuarioRepository usuarioRepository;
    private final ConsultaRepository consultaRepository;
    private final PetRepository petRepository;
    private final TipoConsultaRepository tipoConsultaRepository;
    private final LogsServiceImpl logsService;
    private final AvaliacaoRepository avaliacaoRepository;
    private final EspecializacaoRepository especializacaoRepository;

    @Override
    public List<ConsultasPendentesConfirmadasDto> listarConsultasPendentes(Long idUsuario) {
        return ConsultasPendentesConfirmadasDto.convert(this.consultaRepository.buscarConsultasPendentesOuConfirmadasPorUsuario(idUsuario));
    }

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

    private TipoConsultaModel getTipoConsultaPorId(Long idTipoConsulta) {
        return this.tipoConsultaRepository.findById(idTipoConsulta).orElseThrow(() -> new ObjectNotFoundException("Tipo de Consulta com ID: " + idTipoConsulta));
    }

    private PetModel getPetPorId(Long idPet) {
        return this.petRepository.findById(idPet).orElseThrow(() -> new ObjectNotFoundException("Pet com ID: " + idPet + " não encontrado!"));
    }

    private ConsultaModel getConsultaPorId(Long idConsulta) {
        return this.consultaRepository.findById(idConsulta).orElseThrow(() -> new ObjectNotFoundException("Consulta com ID: " + idConsulta + " não encontrada!"));
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

    @Override
    public List<TiposConsultaDto> listarTiposConsulta() {
        List<TipoConsultaModel> tipos = this.tipoConsultaRepository.findAll();
        return TiposConsultaDto.convert(tipos);
    }

    @Override
    public List<VeterinariosTipoConsultaDto> listarVeterinariosTipoConsulta(Long idTipoConsulta) {
        TipoConsultaModel tipoConsulta = this.getTipoConsultaPorId(idTipoConsulta);
        List<VeterinariosTipoConsultaDto> dto = new ArrayList<>();
        for (UsuarioModel veterinario : tipoConsulta.getVeterinarios()) {
            List<AvaliacaoModel> avaliacoes = this.consultaRepository.findAllByVeterinario_Id(veterinario.getId()).stream().map(ConsultaModel::getAvaliacao).toList();
            double avaliacao = 0;
            for (AvaliacaoModel avaliacaoModel : avaliacoes) {
                avaliacao += avaliacaoModel.getPontuacao();
            }
            avaliacao = avaliacao / avaliacoes.size();
            List<EspecializacaoModel> especializacoes = this.especializacaoRepository.buscarPorVeterinario(veterinario);
            dto.add(new VeterinariosTipoConsultaDto(veterinario, especializacoes, avaliacao));
        }
        return dto;
    }

    @Override
    public List<DiaConsultasVeterinarioDto> buscarDiasHorariosDisponiveisVeterinario(Long idVeterinario) {
        Map<LocalDateTime, List<ConsultaModel>> consultas = this.consultaRepository.findAllByVeterinario_Id(idVeterinario).stream().filter(consulta -> consulta.getDataConsulta().toLocalDate().isEqual(LocalDate.now()) || consulta.getDataConsulta().toLocalDate().isBefore(LocalDate.now())).collect(Collectors.groupingBy(ConsultaModel::getDataConsulta));
        return consultas.entrySet().stream().map(value -> {
            LocalDate data = value.getKey().toLocalDate();
            List<LocalTime> horarios = value.getValue().stream().map(datas -> datas.getDataConsulta().toLocalTime()).toList();
            return new DiaConsultasVeterinarioDto(data, horarios);
        }).toList();
    }

    @Override
    public List<OpcoesPetConsultasDto> buscarPetsConsulta(Long idUsuario) {
        List<PetModel> pets = this.petRepository.findAllByTutor_Id(idUsuario);
        return OpcoesPetConsultasDto.convert(pets);
    }

    private void validarSolicitacaoDeConsulta(Long idUsuario, SolicitacaoConsultaForm form) {
        /*List<ConsultaModel> consultasPagamentosPendentes = this.consultaRepository.findAllBySolicitante_IdAndPagamentoIsNull(idUsuario).stream().filter(consulta -> consulta.getStatus() != StatusConsultaEnum.PENDENTE).toList();*/
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
