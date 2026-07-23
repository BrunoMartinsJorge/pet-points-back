package br.com.api.petpoints.domain.users.gerente.features.consultas.service;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.domain.users.gerente.features.consultas.dto.*;
import br.com.api.petpoints.domain.users.gerente.features.consultas.form.EspecializacaoForm;
import br.com.api.petpoints.domain.users.gerente.features.consultas.form.FiltroConsultaForm;
import br.com.api.petpoints.domain.users.gerente.features.consultas.form.TipoConsultaForm;
import br.com.api.petpoints.shared.enums.TipoLogEnum;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.features.logs.LogsServiceImpl;
import br.com.api.petpoints.shared.models.*;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import br.com.api.petpoints.shared.repository.EspecializacaoRepository;
import br.com.api.petpoints.shared.repository.TipoConsultaRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import br.com.api.petpoints.shared.utils.ColunaRelatorio;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import br.com.api.petpoints.shared.utils.RelatoriosUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GerenteConsultasClinicaServiceImpl implements GerenteConsultasClinicaService {

    private final ConsultaRepository consultaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoConsultaRepository tipoConsultaRepository;
    private final EspecializacaoRepository especializacaoRepository;
    private final RelatoriosUtils relatoriosUtils;
    private final LogsServiceImpl logsService;

    @Override
    public List<ConsultaClinicaDto> listarHistoricoConsultas() {
        List<ConsultaModel> consultas = this.consultaRepository.findAll();
        return ConsultaClinicaDto.convert(consultas);
    }

    @Override
    public List<ParticipanteFiltrosDto> listarSolicitantesParaFiltros() {
        List<UsuarioModel> clientes = this.usuarioRepository.findAllByPermissao(TipoUsuario.C);
        return ParticipanteFiltrosDto.convert(clientes);
    }

    @Override
    public List<ParticipanteFiltrosDto> listarVeterinariosParaFiltros() {
        List<UsuarioModel> veterinarios = this.usuarioRepository.findAllByPermissao(TipoUsuario.V);
        return ParticipanteFiltrosDto.convert(veterinarios);
    }

    @Override
    public List<TipoConsultaFiltrosDto> listarTiposConsultasParaFiltros() {
        List<TipoConsultaModel> tiposConsulta = this.tipoConsultaRepository.findAll();
        return TipoConsultaFiltrosDto.convert(tiposConsulta);
    }

    @Override
    @Transactional
    public void adicionarNovaEspecializacao(EspecializacaoForm form) {
        this.especializacaoRepository.save(EspecializacaoForm.criarNova(form));
    }

    @Override
    public List<EspecializacoesDto> listarEspecializacoes() {
        List<EspecializacaoModel> especialziacoes = this.especializacaoRepository.findAll();
        return EspecializacoesDto.convert(especialziacoes);
    }

    @Override
    public DetalhesEspecialziacaoDto buscarDetalhesEspecializacoes(Long id) {
        EspecializacaoModel especializacao = this.getEspecializacaoPorId(id);
        List<UsuarioModel> veterinariosNaoRelacionados = this.usuarioRepository.findAllByPermissao(TipoUsuario.V);
        veterinariosNaoRelacionados = veterinariosNaoRelacionados.stream()
                .filter(veterinario ->
                        especializacao.getVeterinarios().stream()
                                .noneMatch(vet -> vet.getId().equals(veterinario.getId()))
                )
                .toList();
        return new DetalhesEspecialziacaoDto(especializacao, veterinariosNaoRelacionados);
    }

    @Override
    public List<TiposConsultaDto> listarTiposConsulta() {
        List<TipoConsultaModel> tiposConsulta = this.tipoConsultaRepository.findAll();
        return TiposConsultaDto.convert(tiposConsulta);
    }

    @Override
    public DetalhesConsultaDto buscarDetalhesConsulta(Long idConsulta) {
        ConsultaModel consulta = this.consultaRepository.findById(idConsulta).orElseThrow(() ->
                new ObjectNotFoundException("Consulta com ID: " + idConsulta + " não encontrada!"));
        return new DetalhesConsultaDto(consulta);
    }

    private TipoConsultaModel getTipoConsultaPorId(Long idTipoConsulta) {
        return this.tipoConsultaRepository.findById(idTipoConsulta).orElseThrow(() ->
                new ObjectNotFoundException("Tipo de consulta com ID: " + idTipoConsulta + " não encontrado!"));
    }

    private EspecializacaoModel getEspecializacaoPorId(Long idEspecializacao) {
        return this.especializacaoRepository.findById(idEspecializacao).orElseThrow(() ->
                new ObjectNotFoundException("Especialização com ID: " + idEspecializacao + " não encontrada!"));
    }

    @Override
    public DetalhesTipoConsultaDto buscarDetalhesTipoConsulta(Long idTipoConsulta) {
        TipoConsultaModel tipoConsulta = this.getTipoConsultaPorId(idTipoConsulta);
        DetalhesTipoConsultaDto dto = new DetalhesTipoConsultaDto(tipoConsulta);

        dto.setVeterinario(new ArrayList<>());

        for (UsuarioModel veterinario : tipoConsulta.getVeterinarios()) {
            List<EspecializacaoModel> especializacao =
                    this.especializacaoRepository.buscarPorVeterinario(veterinario);

            dto.getVeterinario().add(
                    new VeterinarioTipoConsultaDto(veterinario, especializacao)
            );
        }

        return dto;
    }

    @Override
    @Transactional
    public void adicionarNovoTipoConsulta(Long idUsuario, TipoConsultaForm form) {
        this.tipoConsultaRepository.save(TipoConsultaForm.criarNovo(form));
        UsuarioModel gerente = this.getUsuarioPorId(idUsuario);
        // this.logsService.registrarLog(gerente, TipoLogEnum.);
    }

    private UsuarioModel getUsuarioPorId(Long idUsuario) {
        return this.usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: " + idUsuario + " não encontrado!"));
    }

    @Override
    @Transactional
    public void editarInformacoesTipoConsulta(Long idUsuario, TipoConsultaForm form, Long idTipoConsulta) {
        TipoConsultaModel tipoConsulta = this.getTipoConsultaPorId(idTipoConsulta);
        if (!form.getNome().isEmpty() && !form.getNome().equals(tipoConsulta.getNome())) {
            tipoConsulta.setNome(form.getNome());
        }
        if (!form.getDescricao().isEmpty() && !form.getDescricao().equals(tipoConsulta.getDescricao())) {
            tipoConsulta.setDescricao(form.getDescricao());
        }
        if (form.getValor() != null && form.getValor() != tipoConsulta.getValor()) {
            tipoConsulta.setValor(form.getValor());
        }
        this.tipoConsultaRepository.save(tipoConsulta);
        this.logsService.registrarLog(this.getUsuarioPorId(idUsuario), TipoLogEnum.EDITOU_TIPO_CONSULTA);
    }

    @Override
    public List<VeterinarioEspecializacoesDto> listarVeterinariosTipoConsulta(Long idTipoConsulta) {
        List<UsuarioModel> veterinairos = this.usuarioRepository.findAllByPermissao(TipoUsuario.V);
        List<VeterinarioEspecializacoesDto> dto = new ArrayList<>();
        for (UsuarioModel veterinario : veterinairos) {
            List<EspecializacaoModel> especializacao = this.especializacaoRepository.buscarPorVeterinario(veterinario);
            dto.add(new VeterinarioEspecializacoesDto(veterinario, especializacao));
        }
        List<Long> veterinariosRelacionados = this.getTipoConsultaPorId(idTipoConsulta).getVeterinarios().stream().map(UsuarioModel::getId).toList();
        if (!veterinariosRelacionados.isEmpty()) {
            dto = dto.stream().filter(veterinario -> !veterinariosRelacionados.contains(veterinario.getId())).toList();
        }
        return dto;
    }

    @Override
    @Transactional
    public void adicionarNovoVeterinarioTipoConsulta(Long idVeterinario, Long idTipoConsulta) {
        UsuarioModel veterinario = this.getUsuarioPorId(idVeterinario);
        TipoConsultaModel tipoConsulta = this.getTipoConsultaPorId(idTipoConsulta);
        tipoConsulta.getVeterinarios().add(veterinario);
        this.tipoConsultaRepository.saveAndFlush(tipoConsulta);
    }

    @Override
    public void removerNovoVeterinarioTipoConsulta(Long idVeterinario, Long idTipoConsulta) {
        TipoConsultaModel tipoConsulta = this.getTipoConsultaPorId(idTipoConsulta);
        List<UsuarioModel> veterinarios = tipoConsulta.getVeterinarios();
        veterinarios = veterinarios.stream().filter(vet -> !Objects.equals(vet.getId(), idVeterinario)).collect(Collectors.toList());
        tipoConsulta.setVeterinarios(veterinarios);
        this.tipoConsultaRepository.saveAndFlush(tipoConsulta);
    }

    @Override
    public byte[] gerarRelatorioConsultas(FiltroConsultaForm form) {
        List<ConsultaModel> consultas = this.consultaRepository.findAll();
        if (form.getIdCliente() != null)
            consultas = consultas.stream().filter(consulta -> Objects.equals(consulta.getSolicitante().getId(), form.getIdCliente())).toList();
        if (form.getIdVeterinario() != null)
            consultas = consultas.stream().filter(consulta -> Objects.equals(consulta.getVeterinario().getId(), form.getIdVeterinario())).toList();
        if (form.getIdTipoConsulta() != null)
            consultas = consultas.stream().filter(consulta -> Objects.equals(consulta.getTipoConsulta().getId(), form.getIdTipoConsulta())).toList();
        String titulo = "Relatório de Consultas Geral";
        List<ColunaRelatorio> colunas = List.of(
                new ColunaRelatorio("ID", m -> ((ConsultaModel) m).getId()),
                new ColunaRelatorio("Cliente", m -> ((ConsultaModel) m).getSolicitante().getNome()),
                new ColunaRelatorio("Veterinário", m -> ((ConsultaModel) m).getVeterinario().getNome()),
                new ColunaRelatorio("Tipo", m -> ((ConsultaModel) m).getTipoConsulta().getNome()),
                new ColunaRelatorio("Data Hora", m -> (LocalDateTimeUtils.converterLocalDateTimeParaPtBr(((ConsultaModel) m).getDataConsulta()))),
                new ColunaRelatorio("Status", m -> ((ConsultaModel) m).getStatus())
        );
        return this.relatoriosUtils.gerarRelatorioGenerico(colunas, consultas, titulo, "");
    }

    @Override
    @Transactional
    public void adicionarNovoVeteterinarioEspecializacao(Long idEspecializacao, Long idVeterinario) {
        UsuarioModel usuario = this.getUsuarioPorId(idVeterinario);
        EspecializacaoModel especializacao = this.getEspecializacaoPorId(idEspecializacao);
        especializacao.getVeterinarios().add(usuario);
        this.especializacaoRepository.saveAndFlush(especializacao);
    }

    @Override
    @Transactional
    public void removerVeteterinarioEspecializacao(Long idEspecializacao, Long idVeterinario) {
        EspecializacaoModel especializacao = this.getEspecializacaoPorId(idEspecializacao);
        Set<UsuarioModel> veterinarios = especializacao.getVeterinarios();
        veterinarios = veterinarios.stream().filter(veterinario -> !Objects.equals(veterinario.getId(), idVeterinario)).collect(Collectors.toSet());
        especializacao.setVeterinarios(veterinarios);
        this.especializacaoRepository.saveAndFlush(especializacao);
    }
}
