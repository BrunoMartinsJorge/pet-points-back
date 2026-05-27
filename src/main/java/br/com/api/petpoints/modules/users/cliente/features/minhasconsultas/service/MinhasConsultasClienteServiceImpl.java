package br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.service;

import br.com.api.petpoints.core.initializer.UsuariosPadroes;
import br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.dto.*;
import br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.forms.AvaliacaoConsultaForm;
import br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.forms.CancelarConsultaForm;
import br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.forms.SolicitacaoConsultaForm;
import br.com.api.petpoints.modules.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.shared.enums.*;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.exception.custom.PerfilDesativadoException;
import br.com.api.petpoints.shared.features.logs.LogsServiceImpl;
import br.com.api.petpoints.shared.models.*;
import br.com.api.petpoints.shared.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MinhasConsultasClienteServiceImpl implements MinhasConsultasClienteService {

    private final UsuarioRepository usuarioRepository;
    private final ConsultaRepository consultaRepository;
    private final PetRepository petRepository;
    private final TipoConsultaRepository tipoConsultaRepository;
    private final LogsServiceImpl logsService;
    private final PagamentoRepository pagamentoRepository;
    private final EspecializacaoRepository especializacaoRepository;
    private final ArquivoRepository arquivoRepository;
    private final ComprovanteRepository comprovanteRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    @Override
    public List<MinhasConsultasDto> listarConsultasAprovadas(Long idUsuario) {
        return MinhasConsultasDto.convert(this.consultaRepository.buscarConsultasConfirmadasPorUsuario(idUsuario));
    }

    @Override
    public List<MinhasConsultasDto> listarMinhasConsultas(Long idUsuario) {
        Optional<UsuarioModel> usuario = usuarioRepository.findById(idUsuario);
        if (usuario.isEmpty()) throw new UsuarioNaoEncontrado("Usuário com ID " + idUsuario + " não encontrado!");
        if (usuario.get().getStatusPerfilEnum().equals(StatusPerfilEnum.D))
            throw new PerfilDesativadoException("Perfil com email " + usuario.get().getEmail() + " desabilitado!");
        List<ConsultaModel> minhasConsultas = this.consultaRepository.findAllBySolicitante_Id(idUsuario).stream().filter(consulta -> !consulta.getStatus().equals(StatusConsultaEnum.PENDENTE)).toList();
        return minhasConsultas.stream().map(MinhasConsultasDto::new).toList();
    }

    @Override
    public List<MinhasConsultasDto> listarConsultasPendentes(Long idUsuario) {
        return MinhasConsultasDto.convert(this.consultaRepository.findAllBySolicitante_IdAndStatus(idUsuario, StatusConsultaEnum.PENDENTE));
    }

    @Override
    public DetalhesConsultaSelecionadaDto buscarDetalhesConsulta(Long idConsulta) {
        ConsultaModel consulta = this.consultaRepository.findById(idConsulta).orElseThrow(() -> new ObjectNotFoundException("Consulta com ID: " + idConsulta + " não encontrada!"));
        return new DetalhesConsultaSelecionadaDto(consulta);
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
    public void solicitarNovaConsulta(Long idUsuario, SolicitacaoConsultaForm form) {
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
        consulta.setPagamento(this.gerarFormaPagamento(tipoConsulta, form.getDataConsulta(), form.getFormaPagamento()));
        this.consultaRepository.save(consulta);
    }

    @Transactional
    protected PagamentoModel gerarFormaPagamento(TipoConsultaModel tipo, LocalDateTime dataConsulta, TipoPagamentoEnum tipoPagamento) {
        PagamentoModel pagamento = new PagamentoModel();
        pagamento.setDataLimitePagamento(dataConsulta);
        pagamento.setTipoPagamento(tipoPagamento);
        pagamento.setValorPagamento(tipo.getValor());
        return this.pagamentoRepository.save(pagamento);
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
        List<TipoConsultaModel> tipos = this.tipoConsultaRepository.findAll().stream().filter(tipo -> !tipo.getVeterinarios().isEmpty()).toList();
        return TiposConsultaDto.convert(tipos);
    }

    @Override
    public List<VeterinariosTipoConsultaDto> listarVeterinariosTipoConsulta(Long idTipoConsulta) {
        TipoConsultaModel tipoConsulta = this.getTipoConsultaPorId(idTipoConsulta);
        List<VeterinariosTipoConsultaDto> dto = new ArrayList<>();
        for (UsuarioModel veterinario : tipoConsulta.getVeterinarios()) {
            List<ConsultaModel> avaliacoes = this.consultaRepository.findAllByVeterinario_Id(veterinario.getId());
            double avaliacao = 0;
            for (ConsultaModel consulta : avaliacoes) {
                if (consulta.getAvaliacao() == null) break;
                avaliacao += consulta.getAvaliacao().getPontuacao();
            }
            avaliacao = avaliacao / avaliacoes.size();
            List<EspecializacaoModel> especializacoes = this.especializacaoRepository.buscarPorVeterinario(veterinario);
            dto.add(new VeterinariosTipoConsultaDto(veterinario, especializacoes, avaliacao));
        }
        return dto;
    }

    @Override
    public List<DiaConsultasVeterinarioDto> buscarDiasHorariosDisponiveisVeterinario(Long idVeterinario) {
        Map<LocalDateTime, List<ConsultaModel>> consultas =
                this.consultaRepository.findAllByVeterinario_Id(idVeterinario)
                        .stream()
                        .filter(this::consultaValida)
                        .collect(Collectors.groupingBy(ConsultaModel::getDataConsulta));
        return consultas.entrySet().stream().map(value -> {
            LocalDate data = value.getKey().toLocalDate();
            List<LocalTime> horarios = value.getValue().stream().map(datas -> datas.getDataConsulta().toLocalTime()).toList();
            return new DiaConsultasVeterinarioDto(data, horarios);
        }).toList();
    }

    private boolean consultaValida(ConsultaModel consulta) {
        LocalDate dataConsulta = consulta.getDataConsulta().toLocalDate();
        StatusConsultaEnum status = consulta.getStatus();

        boolean dataValida =
                !dataConsulta.isAfter(LocalDate.now());

        boolean statusValido =
                status != StatusConsultaEnum.REPROVADA &&
                        status != StatusConsultaEnum.FINALIZADO &&
                        status != StatusConsultaEnum.CANCELADO;

        return dataValida || statusValido;
    }

    @Override
    public List<OpcoesPetConsultasDto> buscarPetsConsulta(Long idUsuario) {
        List<PetModel> pets = this.petRepository.findAllByTutor_Id(idUsuario);
        return OpcoesPetConsultasDto.convert(pets);
    }

    @Override
    public PagamentoDto buscarPagamentoConsulta(Long idConsulta) {
        ConsultaModel consulta = this.getConsultaPorId(idConsulta);
        PagamentoModel pagamento = consulta.getPagamento();
        pagamento.setDataLimitePagamento(consulta.getDataConsulta().plusWeeks(2));
        pagamento.setStatusPagamento(StatusPagamentoEnum.PENDENTE);
        pagamento.setValorPagamento(consulta.getTipoConsulta().getValor());
        this.pagamentoRepository.save(pagamento);
        byte[] comprovante = new byte[0];
        String tipoArquivo = "";
        if (pagamento.getComprovante() != null) {
            comprovante = this.arquivoRepository.findById(pagamento.getComprovante().getArquivo()).get().getConteudo();
            tipoArquivo = this.arquivoRepository.findById(pagamento.getComprovante().getArquivo()).get().getTipo();
        }
        return new PagamentoDto(consulta.getPagamento(), comprovante, tipoArquivo);
    }

    @Override
    @Transactional
    public void registrarComprovante(Long idConsulta, Long idUsuario, MultipartFile file) {
        PagamentoModel pagamento = this.getConsultaPorId(idConsulta)
                .getPagamento();
        UsuarioModel usuario = this.getUsuarioPorId(idUsuario);
        ComprovanteModel comprovante = pagamento.getComprovante();
        UUID novoArquivo = this.salvarArquivo(file);
        if (comprovante == null) {
            comprovante = new ComprovanteModel(novoArquivo);
        } else {
            UUID arquivoAntigo = comprovante.getArquivo();
            comprovante.setArquivo(novoArquivo);
            if (arquivoAntigo != null) {
                this.arquivoRepository.deleteById(arquivoAntigo);
            }
        }
        pagamento.setEmitidoPor(usuario);
        comprovante = this.comprovanteRepository.save(comprovante);
        pagamento.setComprovante(comprovante);
        pagamento.setStatusPagamento(StatusPagamentoEnum.ENVIADO);
        this.pagamentoRepository.save(pagamento);
    }

    @Override
    @Transactional
    public void alterarFormaPagamentoConsulta(Long idConsulta, TipoPagamentoEnum formaPagamento) {
        ConsultaModel consulta = this.getConsultaPorId(idConsulta);
        if (consulta.getStatus() == StatusConsultaEnum.CANCELADO || consulta.getStatus() == StatusConsultaEnum.REPROVADA)
            throw new RuntimeException("A consulta não pode ser alterada devido seu estado atual!");
        if (consulta.getPagamento().getStatusPagamento() == StatusPagamentoEnum.APROVADO)
            throw new RuntimeException("O pagamento já foi aprovado, a forma de pagamento não pode ser alterada!");
        consulta.getPagamento().setTipoPagamento(formaPagamento);
        ComprovanteModel comprovante = consulta.getPagamento().getComprovante();
        if (comprovante != null) {
            UUID arquivo = comprovante.getArquivo();
            if (arquivo != null)
                this.arquivoRepository.deleteById(arquivo);
            this.comprovanteRepository.delete(comprovante);
        }
        consulta.getPagamento().setComprovante(null);
        consulta.getPagamento().setStatusPagamento(StatusPagamentoEnum.PENDENTE);
        this.consultaRepository.save(consulta);
    }

    @Override
    public AvaliacaoConsultaDto buscarAvaliacaoPorConsulta(Long idUsuario, Long idConsulta) {
        UsuarioModel cliente = this.getUsuarioPorId(idUsuario);
        ConsultaModel consulta = this.getConsultaPorId(idConsulta);
        this.consultaPertenceCliente(consulta, cliente);
        AvaliacaoModel avaliacao = consulta.getAvaliacao();
        if (avaliacao == null) return new AvaliacaoConsultaDto();
        return new AvaliacaoConsultaDto(avaliacao);
    }

    @Override
    @Transactional
    public void avaliarConsulta(Long idUsuario, Long idConsulta, AvaliacaoConsultaForm form) {
        UsuarioModel cliente = this.getUsuarioPorId(idUsuario);
        ConsultaModel consulta = this.getConsultaPorId(idConsulta);
        this.consultaPertenceCliente(consulta, cliente);
        if (consulta.getAvaliacao() != null) throw new RuntimeException("Consulta já avaliada!");
        if (consulta.getStatus() != StatusConsultaEnum.FINALIZADO)
            throw new RuntimeException("A consulta só pode ser avaliada caso já esteja finalizada!");
        AvaliacaoModel avaliacao = this.avaliacaoRepository.save(new AvaliacaoModel(form, cliente));
        consulta.setAvaliacao(avaliacao);
        this.consultaRepository.save(consulta);
    }

    @Override
    public MinhasConsultasDto buscarConsultaPorId(Long idConsulta) {
        return new MinhasConsultasDto(this.getConsultaPorId(idConsulta));
    }

    private void consultaPertenceCliente(ConsultaModel consulta, UsuarioModel cliente) {
        if (!consulta.getSolicitante().equals(cliente))
            throw new RuntimeException("Você não pode acessar essa consulta!");
    }

    private UUID salvarArquivo(MultipartFile form) {
        if (form.getSize() > 5_000_000) throw new RuntimeException("Arquivo passa de 5MB!");
        List<String> tiposPermitidos = List.of(
                "image/png",
                "image/jpeg",
                "application/pdf"
        );
        if (!tiposPermitidos.contains(form.getContentType()))
            throw new RuntimeException("Tipo inválido");
        ArquivosModel arquivo = new ArquivosModel();
        try {
            arquivo.setConteudo(form.getBytes());
            arquivo.setNome(form.getOriginalFilename());
            arquivo.setTipo(form.getContentType());
            return this.arquivoRepository.save(arquivo).getId();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
