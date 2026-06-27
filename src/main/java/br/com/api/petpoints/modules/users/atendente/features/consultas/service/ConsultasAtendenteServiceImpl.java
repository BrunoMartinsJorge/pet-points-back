package br.com.api.petpoints.modules.users.atendente.features.consultas.service;

import br.com.api.petpoints.modules.users.atendente.features.consultas.dto.AvaliacaoConsultaDto;
import br.com.api.petpoints.modules.users.atendente.features.consultas.dto.ConsultasAtendenteDto;
import br.com.api.petpoints.modules.users.atendente.features.consultas.dto.InformacoesPagamentoDto;
import br.com.api.petpoints.modules.users.atendente.features.consultas.forms.IndeferirConsultaForm;
import br.com.api.petpoints.modules.users.atendente.features.consultas.forms.IndeferirPagamentoForm;
import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import br.com.api.petpoints.shared.enums.TipoLogEnum;
import br.com.api.petpoints.shared.enums.TiposNotificacoesEnum;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.features.logs.LogsServiceImpl;
import br.com.api.petpoints.shared.features.notificacoes.controller.NotificacoesController;
import br.com.api.petpoints.shared.features.notificacoes.form.NovaNotificacaoForm;
import br.com.api.petpoints.shared.models.ConsultaModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import br.com.api.petpoints.shared.repository.PetRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsultasAtendenteServiceImpl implements ConsultasAtendenteService {

    private final UsuarioRepository usuarioRepository;
    private final PetRepository petRepository;
    private final ConsultaRepository consultaRepository;
    private final LogsServiceImpl logsService;
    private final NotificacoesController notificacoesController;

    private ConsultaModel getConsultaPorId(Long idConsulta) {
        log.info("Busca de consulta em andamento...");
        return consultaRepository.findById(idConsulta).orElseThrow(() -> new ObjectNotFoundException("Consulta com ID: " + idConsulta + " não encontrada!"));
    }

    private UsuarioModel getUsuarioPorId(Long idUsuario) {
        log.info("Busca de usuário em andamento...");
        return usuarioRepository.findById(idUsuario).orElseThrow(() -> new ObjectNotFoundException("Usuário com ID: " + idUsuario + " não encontrado!"));
    }

    @Override
    public List<ConsultasAtendenteDto> listarConsultasPendentes() {
        List<ConsultaModel> consultas = this.consultaRepository.findAllByStatus(StatusConsultaEnum.PENDENTE);
        return consultas.stream().map(ConsultasAtendenteDto::new).toList();
    }

    @Override
    public List<ConsultasAtendenteDto> listarHistoricoDeConsultas() {
        List<ConsultaModel> consultas = this.consultaRepository.findAll();
        return consultas.stream().map(ConsultasAtendenteDto::new).toList();
    }

    @Override
    @Transactional
    public void deferirSolicitacaoDeConsulta(Long idConsulta, Long idUsuario) {
        ConsultaModel consulta = this.getConsultaPorId(idConsulta);
        UsuarioModel atendente = this.getUsuarioPorId(idUsuario);
        if (consulta.getStatus() != StatusConsultaEnum.PENDENTE) throw new RuntimeException("Consulta já deferida!");
        log.info("Processando aprovação de consulta...");
        consulta.setStatus(StatusConsultaEnum.APROVADA);
        consulta.setAtendente(atendente);
        consulta.setDeferidoEm(LocalDateTime.now());
        this.logsService.registrarLog(atendente, TipoLogEnum.DEFERIU_CONSULTA);
        consulta = consultaRepository.save(consulta);
        this.enviarNotificacaoClienteVeterinario(consulta);
        log.debug("Aprovação de consulta concluida!");
    }

    @Override
    @Transactional
    public void indeferirSolicitacaoDeConsulta(IndeferirConsultaForm form, Long idUsuario) {
        ConsultaModel consulta = this.getConsultaPorId(form.getIdConsulta());
        UsuarioModel atendente = this.getUsuarioPorId(idUsuario);
        if (consulta.getStatus() != StatusConsultaEnum.PENDENTE) throw new RuntimeException("Consulta já deferida!");
        log.info("Processando indeferimento de consulta...");
        consulta.setStatus(StatusConsultaEnum.REPROVADA);
        consulta.setAtendente(atendente);
        consulta.setDeferidoEm(LocalDateTime.now());
        consulta.setMotivoIndeferimento(form.getMotivo());
        this.logsService.registrarLog(atendente, TipoLogEnum.INDEFERIU_CONSULTA);
        consulta = consultaRepository.save(consulta);
        this.enviarNotificacaoClienteVeterinario(consulta);
        log.debug("Indeferimento de consulta concluida!");
    }

    public List<ConsultasAtendenteDto> listarConsultasComPagamentosPendentesDoCliente(Long idCliente) {
        return this.consultaRepository.findAllBySolicitante_IdAndPagamentoIsNull(idCliente).stream().filter(consulta -> consulta.getStatus() == StatusConsultaEnum.FINALIZADO).map(ConsultasAtendenteDto::new).toList();
    }

    @Override
    public InformacoesPagamentoDto buscarInformacoesPagamento(Long idConsulta) {
        ConsultaModel consulta = this.getConsultaPorId(idConsulta);
        if (consulta.getPagamento() == null) return new InformacoesPagamentoDto();
        return new InformacoesPagamentoDto(consulta.getPagamento());
    }

    @Override
    public AvaliacaoConsultaDto buscarAvaliacao(Long idConsulta) {
        ConsultaModel consulta = this.getConsultaPorId(idConsulta);
        if (consulta.getAvaliacao() == null) return new AvaliacaoConsultaDto();
        return new AvaliacaoConsultaDto(consulta.getAvaliacao());
    }

    @Override
    @Transactional
    public void avaliarPagamento(Long idConsulta, IndeferirPagamentoForm form) {
        ConsultaModel consulta = this.getConsultaPorId(idConsulta);
        consulta.getPagamento().setStatusPagamento(form.isAprovar() ? StatusPagamentoEnum.APROVADO : StatusPagamentoEnum.REPROVADO);
        consulta.getPagamento().setMotivoIndeferimento(!form.isAprovar() ? form.getMotivoIndeferimento() : "");
        this.consultaRepository.save(consulta);
        NovaNotificacaoForm notificacao = new NovaNotificacaoForm(
                consulta.getSolicitante().getId(),
                "Pagamento de Consulta",
                "Pagamento APROVADO por atendente!",
                TiposNotificacoesEnum.CONSULTA
        );
        try {
            this.notificacoesController.enviarNotificacao(notificacao);
            log.info("Notificação de aprovação de pagamento enviada ao cliente!");
        } catch (Exception e) {
            log.error("Problema ao enviar notificação de aprovação de pagamento ao cliente!");
            throw new RuntimeException("Ocorreu um erro ao gerar notificação de cliente da consulta!");
        }
    }

    @Transactional
    protected void enviarNotificacaoClienteVeterinario(ConsultaModel consulta) {
        if (consulta.getStatus().equals(StatusConsultaEnum.APROVADA)) {
            NovaNotificacaoForm notificacao = new NovaNotificacaoForm(
                    consulta.getSolicitante().getId(),
                    "Solicitação de Consulta",
                    "Solicitação de consulta APROVADA",
                    TiposNotificacoesEnum.CONSULTA
            );
            try {
                this.notificacoesController.enviarNotificacao(notificacao);
                log.info("Notificação de aprovação de consulta enviada ao cliente!");
            } catch (Exception e) {
                log.error("Problema ao enviar notificação de aprovação de consulta ao cliente!");
                throw new RuntimeException("Ocorreu um erro ao gerar notificação de cliente da consulta!");
            }
            notificacao = new NovaNotificacaoForm(
                    consulta.getVeterinario().getId(),
                    "Consulta Agendada",
                    "Uma nova consulta foi agendada!",
                    TiposNotificacoesEnum.CONSULTA
            );
            try {
                this.notificacoesController.enviarNotificacao(notificacao);
                log.info("Notificação de aprovação de consulta enviada ao veterinário!");
            } catch (Exception e) {
                log.error("Problema ao enviar notificação de indeferimento de consulta ao veterinário!");
                throw new RuntimeException("Ocorreu um erro ao gerar notificação de veterinário da consulta!");
            }
        } else if (consulta.getStatus().equals(StatusConsultaEnum.REPROVADA)) {
            NovaNotificacaoForm notificacao = new NovaNotificacaoForm(
                    consulta.getSolicitante().getId(),
                    "Solicitação de Consulta",
                    "Solicitação de consulta REPROVADA",
                    TiposNotificacoesEnum.CONSULTA
            );
            try {
                this.notificacoesController.enviarNotificacao(notificacao);
                log.info("Notificação de indeferimento de consulta enviada ao cliente!");
            } catch (Exception e) {
                log.error("Problema ao enviar notificação de indeferimento de consulta ao cliente!");
                throw new RuntimeException("Ocorreu um erro ao gerar notificação de cliente da consulta!");
            }
        }
    }
}
