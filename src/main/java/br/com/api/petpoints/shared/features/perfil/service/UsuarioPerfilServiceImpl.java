package br.com.api.petpoints.shared.features.perfil.service;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.domain.users.atendente.features.consultas.dto.AvaliacaoConsultaDto;
import br.com.api.petpoints.domain.users.cliente.features.meuspagamentos.dto.PagamentosDto;
import br.com.api.petpoints.domain.users.cliente.features.meuspagamentos.service.MeusPagamentosServiceImpl;
import br.com.api.petpoints.shared.enums.StatusAtendimentoEnum;
import br.com.api.petpoints.shared.features.perfil.dto.*;
import br.com.api.petpoints.shared.features.perfil.form.EditarPerfilForm;
import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import br.com.api.petpoints.shared.enums.StatusPerfilEnum;
import br.com.api.petpoints.shared.models.*;
import br.com.api.petpoints.shared.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsuarioPerfilServiceImpl implements UsuarioPerfilService {

    private final UsuarioRepository usuarioRepository;
    private final ArquivoRepository arquivoRepository;
    private final ConsultaRepository consultaRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final PetRepository petRepository;
    private final MeusPagamentosServiceImpl servicePagamentosCliente;

    private UsuarioModel getUsuarioPorId(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: "
                + id + " não encontrado!"));
    }

    @Override
    public InformacoesUsuarioDto buscarInformacoes(Long idUsuario) {
        return new InformacoesUsuarioDto(this.getUsuarioPorId(idUsuario));
    }

    private UUID salvarArquivo(MultipartFile form) {
        if (form.getSize() > 5_000_000) throw new RuntimeException("Arquivo passa de 5MB!");
        List<String> tiposPermitidos = List.of("image/png", "image/jpeg", "application/pdf");
        if (!tiposPermitidos.contains(form.getContentType())) throw new RuntimeException("Tipo inválido");
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

    @Override
    @Transactional
    public void editarInformacoesUsuario(Long idUsuario, EditarPerfilForm form, MultipartFile imagem) {
        UsuarioModel usuario = this.getUsuarioPorId(idUsuario);
        usuario.setNome(form.getNome());
        usuario.setEmail(form.getEmail());
        usuario.setCpf(form.getCpf());
        usuario.setDataNascimento(form.getDataNascimento());
        usuario.setTelefone(form.getTelefone());
        usuario.setGenero(form.getGenero());
        if (imagem != null) {
            if (usuario.getImagem() != null) {
                UUID antigo = usuario.getImagem();
                UUID novo = this.salvarArquivo(imagem);
                usuario.setImagem(novo);
                this.arquivoRepository.deleteById(antigo);
            } else {
                UUID novo = this.salvarArquivo(imagem);
                usuario.setImagem(novo);
            }
        }
        this.usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void desativarPerfil(Long idUsuario) {
        log.info("[DESATIVAR PERFIL] - Iniciando processo de desativar perfil de usuário");
        UsuarioModel usuario = this.getUsuarioPorId(idUsuario);
        log.info("Usuário encontrado - " + usuario.getNome() + " - " + usuario.getCpf());
        switch (usuario.getPermissao()) {
            case TipoUsuario.C -> this.desativarPerfilCliente(usuario);
            case TipoUsuario.A -> this.desativarPerfilCliente(usuario);
            case TipoUsuario.E -> this.desativarPerfilCliente(usuario);
            case TipoUsuario.G -> this.desativarPerfilCliente(usuario);
            case TipoUsuario.V -> this.desativarPerfilCliente(usuario);
        }
        usuario.setStatusPerfilEnum(StatusPerfilEnum.D);
        this.usuarioRepository.save(usuario);
        log.info("[DESATIVAR PERFIL] - Operação de desativação de perfil finalizada! Perfil desabilitado!");
    }

    @Override
    public RankingFuncionarioDto buscarInformacoesRankingAvaliacoes(Long idUsuario) {
        log.info("[BUSCANDO RANKING DE FUNCIONARIO] - Iniciando processo de classificação de funcionarios!");
        UsuarioModel funcionario = this.getUsuarioPorId(idUsuario);
        if (!funcionario.getPermissao().equals(TipoUsuario.A) && !funcionario.getPermissao().equals(TipoUsuario.V))
            throw new RuntimeException("O tipo de usuário não tem acesso a avaliações!");
        List<AvaliacaoModel> avaliacoes;
        if (funcionario.getPermissao().equals(TipoUsuario.A))
            avaliacoes = this.atendimentoRepository.findAllByAtendente_IdAndAvaliacaoIsNotNull(idUsuario).stream()
                    .map(AtendimentoModel::getAvaliacao).toList();
        else
            avaliacoes = this.consultaRepository.findAllByVeterinario_IdAndAvaliacaoIsNotNull(idUsuario).stream()
                    .map(ConsultaModel::getAvaliacao).toList();
        if (avaliacoes.isEmpty())
            return new RankingFuncionarioDto(null, 0, null, null);
        List<AvaliacaoModel> ordenadas = avaliacoes.stream()
                .sorted(Comparator.comparing(AvaliacaoModel::getPontuacao))
                .toList();
        return funcionario.getPermissao().equals(TipoUsuario.A)
                ? this.gerarAvaliacaoRankingAtendente(funcionario, ordenadas)
                : this.gerarAvaliacaoRankingVeterinario(funcionario, ordenadas);
    }

    private RankingFuncionarioDto gerarAvaliacaoRankingAtendente(UsuarioModel atendente, List<AvaliacaoModel> minhasAvaliacoes) {
        List<AtendimentoModel> atendimentos = this.atendimentoRepository.buscarAvaliacoesFinalizadas(StatusAtendimentoEnum.FINALIZADO);
        Map<UsuarioModel, Double> mediaPorAtendente = atendimentos.stream()
                .filter(a -> a.getAvaliacao() != null)
                .collect(Collectors.groupingBy(
                        AtendimentoModel::getAtendente,
                        Collectors.averagingInt(a -> a.getAvaliacao().getPontuacao())
                ));
        List<UsuarioModel> ranking = mediaPorAtendente.entrySet().stream()
                .sorted(Map.Entry.<UsuarioModel, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();

        int classificacao = ranking.indexOf(atendente) + 1;
        AtomicInteger pontuacao = new AtomicInteger();
        minhasAvaliacoes.forEach(avaliacao -> {
            pontuacao.addAndGet(avaliacao.getPontuacao());
        });
        return new RankingFuncionarioDto(
                classificacao,
                pontuacao.get(),
                new AvaliacaoConsultaDto(minhasAvaliacoes.getFirst()),
                new AvaliacaoConsultaDto(minhasAvaliacoes.getLast())
        );
    }

    private RankingFuncionarioDto gerarAvaliacaoRankingVeterinario(UsuarioModel veterinario, List<AvaliacaoModel> minhasAvaliacoes) {
        List<ConsultaModel> consultas = this.consultaRepository.buscarAvaliacoesFinalizadas();
        Map<UsuarioModel, Double> mediaPorVeterinario = consultas.stream()
                .filter(a -> a.getAvaliacao() != null)
                .collect(Collectors.groupingBy(
                        ConsultaModel::getAtendente,
                        Collectors.averagingInt(a -> a.getAvaliacao().getPontuacao())
                ));
        List<UsuarioModel> ranking = mediaPorVeterinario.entrySet().stream()
                .sorted(Map.Entry.<UsuarioModel, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();

        int classificacao = ranking.indexOf(veterinario) + 1;
        AtomicInteger pontuacao = new AtomicInteger();
        minhasAvaliacoes.forEach(avaliacao -> {
            pontuacao.addAndGet(avaliacao.getPontuacao());
        });
        return new RankingFuncionarioDto(
                classificacao,
                pontuacao.get(),
                new AvaliacaoConsultaDto(minhasAvaliacoes.getFirst()),
                new AvaliacaoConsultaDto(minhasAvaliacoes.getLast())
        );
    }

    @Override
    public List<AvaliacaoConsultaDto> buscarAvaliacoes(Long idUsuario) {
        UsuarioModel funcionario = this.getUsuarioPorId(idUsuario);
        if (!funcionario.getPermissao().equals(TipoUsuario.A) && !funcionario.getPermissao().equals(TipoUsuario.V))
            throw new RuntimeException("O tipo de usuário não tem acesso a avaliações!");
        List<AvaliacaoModel> avaliacoes;
        if (funcionario.getPermissao().equals(TipoUsuario.A))
            avaliacoes = this.atendimentoRepository.findAllByAtendente_Id(idUsuario).stream()
                    .map(AtendimentoModel::getAvaliacao).toList();
        else
            avaliacoes = this.consultaRepository.findAllByVeterinario_IdAndAvaliacaoIsNotNull(idUsuario).stream()
                    .map(ConsultaModel::getAvaliacao).toList();
        return AvaliacaoConsultaDto.convert(avaliacoes);
    }

    @Override
    public List<ConsultasAtendenteVeterinarioDto> buscarConsultasAtendenteVeterinario(Long idUsuario) {
        UsuarioModel funcionario = this.getUsuarioPorId(idUsuario);
        if (!funcionario.getPermissao().equals(TipoUsuario.A) && !funcionario.getPermissao().equals(TipoUsuario.V))
            throw new RuntimeException("O tipo de usuário não tem acesso a avaliações!");
        List<ConsultaModel> consultas;
        if (funcionario.getPermissao().equals(TipoUsuario.A))
            consultas = this.consultaRepository.findAllByAtendente_Id(idUsuario);
        else
            consultas = this.consultaRepository.findAllByVeterinario_IdAndAvaliacaoIsNotNull(idUsuario);
        return ConsultasAtendenteVeterinarioDto.convert(consultas);
    }

    @Override
    public RelatorioFinanceiroClienteDto buscarRelatorioFinanceiroCliente(Long idUsuario) {
        UsuarioModel cliente = this.getUsuarioPorId(idUsuario);
        if (!cliente.getPermissao().equals(TipoUsuario.C))
            throw new RuntimeException("O tipo de usuário não tem acesso a essa feature!");
        List<PagamentosDto> pagamentosAtrasados = this.servicePagamentosCliente.listarPagamentosPendentesAtrasados(idUsuario);
        double saldoPendente = pagamentosAtrasados.stream().map(PagamentosDto::getValor).reduce(0.0, Double::sum);
        return new RelatorioFinanceiroClienteDto(pagamentosAtrasados.size(), saldoPendente, pagamentosAtrasados);
    }

    @Override
    public List<MinhasAvaliacoesDto> buscarMinhasAvaliacoes(Long idUsuario) {
        UsuarioModel cliente = this.getUsuarioPorId(idUsuario);
        if (!cliente.getPermissao().equals(TipoUsuario.C))
            throw new RuntimeException("O tipo de usuário não tem acesso a essa feature!");
        List<AvaliacaoModel> avaliacoesConsulta = this.consultaRepository.findAllBySolicitante_IdAndStatus(idUsuario, StatusConsultaEnum.FINALIZADO).stream().map(ConsultaModel::getAvaliacao).toList();
        List<AvaliacaoModel> avaliacoesAtendimento = this.atendimentoRepository.findAllByCliente_IdAndStatus(idUsuario, StatusAtendimentoEnum.FINALIZADO).stream().map(AtendimentoModel::getAvaliacao).toList();
        List<MinhasAvaliacoesDto> dto = new ArrayList<>();
        for (AvaliacaoModel avaliacao : avaliacoesConsulta) {
            dto.add(new MinhasAvaliacoesDto(avaliacao, "CONSULTA"));
        }
        for (AvaliacaoModel avaliacao : avaliacoesAtendimento) {
            dto.add(new MinhasAvaliacoesDto(avaliacao, "ATENDIMENTO"));
        }
        return dto;
    }

    @Transactional
    protected void desativarPerfilCliente(UsuarioModel cliente) {
        log.info("[DESATIVAR PERFIL - CLIENTE] - Iniciando operações expecificas de desativar perfil de cliente");
        List<ConsultaModel> consultas = this.consultaRepository.findAllBySolicitante_Id(cliente.getId());
        log.info("Listando consultas do cliente. {} consultas", consultas.size());
        List<ConsultaModel> consultasFinalizadas = consultas.stream().filter(consulta -> consulta.getStatus().equals(StatusConsultaEnum.FINALIZADO) && !consulta.getPagamento().getStatusPagamento().equals(StatusPagamentoEnum.APROVADO)).toList();
        log.info("Consultas filtradas para cancelamento. {} consultas", consultas.size());
        if (!consultasFinalizadas.isEmpty()) throw new RuntimeException("Cliente possui pagamentos pendentes!");
        consultas.forEach(consulta -> {
            if (consulta.getStatus().equals(StatusConsultaEnum.FINALIZADO)) return;
            log.info("Alterando status da consulta #{} para finalizado", consulta.getId());
            consulta.setStatus(StatusConsultaEnum.CANCELADO);
            consulta.setCanceladoEm(LocalDateTime.now());
            consulta.setMotivoCancelamento("Cliente desativou seu perfil!");
            this.consultaRepository.save(consulta);
        });
        List<PetModel> pets = this.petRepository.findAllByTutor_Id(cliente.getId()).stream().filter(pet -> pet.getStatus().equals(StatusPerfilEnum.A)).toList();
        log.info("Listando pets do cliente para serem desativados. {} Pets", pets.size());
        pets.forEach(pet -> {
            log.info("Alterando status do pet #{} - {} para DESATIVADO", pet.getId(), pet.getNome());
            pet.setStatus(StatusPerfilEnum.D);
            this.petRepository.save(pet);
        });
        log.info("[DESATIVAR PERFIL - CLIENTE] - Finalizada todas as operações expecificas de desativar perfil de cliente");
    }
}
