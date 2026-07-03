package br.com.api.petpoints.domain.users.gerente.features.funcionarios.services;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.domain.users.gerente.features.funcionarios.dto.AvaliacoesDto;
import br.com.api.petpoints.domain.users.gerente.features.funcionarios.dto.ConsultaFuncionarioDto;
import br.com.api.petpoints.domain.users.gerente.features.funcionarios.dto.FuncionarioDto;
import br.com.api.petpoints.domain.users.gerente.features.funcionarios.dto.MovimentacoesEstoquistasDto;
import br.com.api.petpoints.domain.users.gerente.features.funcionarios.forms.EditarFuncionarioForm;
import br.com.api.petpoints.domain.users.gerente.features.funcionarios.forms.FiltroFuncionariosForm;
import br.com.api.petpoints.domain.users.gerente.features.funcionarios.forms.FuncionarioForm;
import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.shared.dto.OpcoesDto;
import br.com.api.petpoints.shared.enums.StatusAtendimentoEnum;
import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.enums.StatusPerfilEnum;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.features.logs.LogsServiceImpl;
import br.com.api.petpoints.shared.models.*;
import br.com.api.petpoints.shared.repository.*;
import br.com.api.petpoints.shared.utils.ColunaRelatorio;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import br.com.api.petpoints.shared.utils.RelatoriosUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FuncionariosGerenteServiceImpl implements FuncionariosGerenteService {

    private final UsuarioRepository usuarioRepository;
    private final ConsultaRepository consultaRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final PasswordEncoder passwordEncoder;
    private final LogsServiceImpl logsService;
    private final RelatoriosUtils relatoriosUtils;
    private final EspecializacaoRepository especializacaoRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final ArquivoRepository arquivoRepository;

    private UsuarioModel getUsuarioPorId(Long idFuncionario) {
        return this.usuarioRepository.findById(idFuncionario).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: " + idFuncionario + " não encontrado!"));
    }

    @Override
    public List<FuncionarioDto> listarFuncionarios(Long idUsuario) {
        List<UsuarioModel> funcionarios = this.usuarioRepository.findAllByPermissaoNot(TipoUsuario.C).stream().filter(funcionario -> !Objects.equals(funcionario.getId(), idUsuario)).toList();
        return funcionarios.stream().map(FuncionarioDto::new).toList();
    }

    @Override
    public FuncionarioDto buscarFuncionarioPorId(Long idFuncionario) {
        UsuarioModel usuario = this.getUsuarioPorId(idFuncionario);
        return new FuncionarioDto(usuario);
    }

    @Override
    public Set<OpcoesDto> listarEspecializacoes() {
        List<EspecializacaoModel> especializacoes = this.especializacaoRepository.findAll();
        return especializacoes.stream().map(especializacao -> new OpcoesDto(especializacao.getDescricao(), especializacao.getId())).collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public FuncionarioDto cadastrarNovoFuncionario(FuncionarioForm form) {
        UsuarioModel novoFuncionario = new UsuarioModel(form, passwordEncoder.encode(form.getSenha()));
        novoFuncionario = (usuarioRepository.save(novoFuncionario));
        if (form.getEspecializacao() != null && form.getPermissao().equals(TipoUsuario.V)) {
            EspecializacaoModel especializacao = this.especializacaoRepository.findById(form.getEspecializacao()).orElseThrow(() -> new ObjectNotFoundException("Especialização com ID: " + form.getEspecializacao() + " não encontrado!"));
            especializacao.getVeterinarios().add(novoFuncionario);
            this.especializacaoRepository.save(especializacao);
        }
        return new FuncionarioDto(novoFuncionario);
    }

    @Override
    @Transactional
    public FuncionarioDto atualizarFuncionario(EditarFuncionarioForm form, Long idFuncionario, MultipartFile file) {
        UsuarioModel usuario = this.alterarDados((this.getUsuarioPorId(idFuncionario)), form, file);
        this.usuarioRepository.save(usuario);
        return new FuncionarioDto(usuario);
    }

    @Override
    @Transactional
    public void desativarFuncionario(Long idUsuario, Long idFuncionario) {
        UsuarioModel funcionario = this.getUsuarioPorId(idFuncionario);
        UsuarioModel gerente = this.getUsuarioPorId(idUsuario);
        switch (funcionario.getPermissao()) {
            case V:
                this.validarVeterinario(idFuncionario);
                break;
            case A:
                this.validarAtendente(idFuncionario);
                break;
            case C:
                break;
            case E, G:
                break;
            default:
                throw new RuntimeException("Tipo de usuário inválido!");
        }
        funcionario.setStatusPerfilEnum(StatusPerfilEnum.D);
        this.usuarioRepository.save(funcionario);
    }

    @Override
    public byte[] gerarRelatorioFuncionarios(FiltroFuncionariosForm form) {
        List<UsuarioModel> funcionarios = this.filtrarFuncionarios(form);
        String titulo = "Relatório de Funcionários da Clínica";
        List<ColunaRelatorio> colunas = List.of(new ColunaRelatorio("Nome", m -> ((UsuarioModel) m).getNome()), new ColunaRelatorio("Email", m -> ((UsuarioModel) m).getEmail()), new ColunaRelatorio("Gênero", m -> ((UsuarioModel) m).getGenero().getDescricao()), new ColunaRelatorio("Cargo", m -> ((UsuarioModel) m).getPermissao().getDescricao()), new ColunaRelatorio("CPF", m -> ((UsuarioModel) m).getCpf()), new ColunaRelatorio("Registrado Em", m -> (LocalDateTimeUtils.converterLocalDateTimeParaPtBr(((UsuarioModel) m).getDataCadastro()))));
        return this.relatoriosUtils.gerarRelatorioGenerico(colunas, funcionarios, titulo, "");
    }

    @Override
    public List<AvaliacoesDto> buscarAvaliacoesPorId(Long id) {
        UsuarioModel funcionario = this.getUsuarioPorId(id);
        List<AvaliacaoModel> avaliacoes = new ArrayList<>();
        if (funcionario.getPermissao().equals(TipoUsuario.A))
            avaliacoes = this.buscarAvaliacoesAtendente(id);
        else if (funcionario.getPermissao().equals(TipoUsuario.V))
            avaliacoes = this.buscarAvaliacoesVeterinario(id);
        return AvaliacoesDto.convert(avaliacoes);
    }

    @Override
    public List<ConsultaFuncionarioDto> buscarConsultasPorId(Long id) {
        UsuarioModel usuario = this.getUsuarioPorId(id);
        List<ConsultaModel> consultas;
        if (usuario.getPermissao().equals(TipoUsuario.V)){
            consultas = this.consultaRepository.findAllByVeterinario_Id(id);
        } else if(usuario.getPermissao().equals(TipoUsuario.A)){
            consultas = this.consultaRepository.findAllByAtendente_Id(id);
        } else{
            throw new RuntimeException("Funcionário não tem acesso as consultas!");
        }
        return ConsultaFuncionarioDto.convert(consultas);
    }

    @Override
    public List<MovimentacoesEstoquistasDto> buscarMovimentacoesPorId(Long id) {
        List<MovimentacaoModel> movimentacoes = this.movimentacaoRepository.findAllByMovimentadoPor_Id(id);
        return MovimentacoesEstoquistasDto.convert(movimentacoes);
    }

    private List<AvaliacaoModel> buscarAvaliacoesAtendente(Long idAtendente) {
        return this.atendimentoRepository.buscarAvaliacoesAtendimento(idAtendente, StatusAtendimentoEnum.FINALIZADO).stream().map(AtendimentoModel::getAvaliacao).toList();
    }

    private List<AvaliacaoModel> buscarAvaliacoesVeterinario(Long idVeterinario) {
        return this.consultaRepository.buscarConsultaPorStatusIdVeterinario(idVeterinario, StatusConsultaEnum.FINALIZADO).stream().map(ConsultaModel::getAvaliacao).toList();
    }

    private List<UsuarioModel> filtrarFuncionarios(FiltroFuncionariosForm form) {
        List<UsuarioModel> funcionarios = this.usuarioRepository.findAllByPermissaoNot(TipoUsuario.C);
        if (form.getNome() != null && !form.getNome().isEmpty())
            funcionarios = funcionarios.stream().filter(funcionario -> funcionario.getNome().toLowerCase().contains(form.getNome().toLowerCase())).toList();
        if (form.getEmail() != null && !form.getEmail().isEmpty())
            funcionarios = funcionarios.stream().filter(funcionario -> funcionario.getEmail().toLowerCase().contains(form.getEmail().toLowerCase())).toList();
        if (form.getTipo() != null && !form.getTipo().isEmpty())
            funcionarios = funcionarios.stream().filter(funcionario -> Objects.equals(funcionario.getPermissao().toString(), form.getTipo())).toList();
        return funcionarios;
    }

    private void validarAtendente(Long idAtendented) {
        List<AtendimentoModel> atendimentosPendentes = this.atendimentoRepository.findAllByAtendente_IdAndStatus(idAtendented, StatusAtendimentoEnum.EM_ANDAMENTO);
        if (!atendimentosPendentes.isEmpty())
            throw new RuntimeException("O atendente ainda está participando de atendimentos!");
    }

    private void validarVeterinario(Long idVeterinario) {
        List<ConsultaModel> consultasPendentes = this.consultaRepository.findAllByVeterinario_Id(idVeterinario).stream().filter(consulta -> !consulta.getStatus().equals(StatusConsultaEnum.INICIADO) && !consulta.getStatus().equals(StatusConsultaEnum.PENDENTE)).toList();
        if (!consultasPendentes.isEmpty()) throw new RuntimeException("O veterinário possui consultas pendentes!");
    }

    private UsuarioModel alterarDados(UsuarioModel usuario, EditarFuncionarioForm form, MultipartFile file) {
        usuario.setNome(!Objects.equals(usuario.getNome(), form.getNome()) ? form.getNome() : usuario.getNome());
        usuario.setEmail(!Objects.equals(usuario.getEmail(), form.getEmail()) ? form.getEmail() : usuario.getEmail());
        usuario.setTelefone(!Objects.equals(usuario.getTelefone(), form.getTelefone()) ? form.getTelefone() : usuario.getTelefone());
        usuario.setPermissao(usuario.getPermissao() != form.getPermissao() ? form.getPermissao() : usuario.getPermissao());
        usuario.setGenero(usuario.getGenero() != form.getGenero() ? form.getGenero() : usuario.getGenero());
        usuario.setDataNascimento(usuario.getDataNascimento() != form.getDataNascimento() ? form.getDataNascimento() : usuario.getDataNascimento());
        if (file != null && !file.isEmpty()) {
            UUID imagemAntiga = usuario.getImagem();
            UUID novaImagem = this.salvarArquivo(file);
            usuario.setImagem(novaImagem);
            if (imagemAntiga != null) {
                this.arquivoRepository.deleteById(imagemAntiga);
            }
        }
        return usuario;
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
}
