package br.com.api.petpoints.modules.gerente.features.funcionarios.services;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.modules.gerente.features.funcionarios.dto.FuncionarioDto;
import br.com.api.petpoints.modules.gerente.features.funcionarios.forms.FuncionarioForm;
import br.com.api.petpoints.modules.usuario.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.shared.enums.StatusAtendimentoEnum;
import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.enums.StatusPerfilEnum;
import br.com.api.petpoints.shared.features.logs.LogsServiceImpl;
import br.com.api.petpoints.shared.models.AtendimentoModel;
import br.com.api.petpoints.shared.models.ConsultaModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.AtendimentoRepository;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FuncionariosGerenteServiceImpl implements FuncionariosGerenteService {

    private final UsuarioRepository usuarioRepository;
    private final ConsultaRepository consultaRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final PasswordEncoder passwordEncoder;
    private final LogsServiceImpl logsService;

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
    @Transactional
    public FuncionarioDto cadastrarNovoFuncionario(FuncionarioForm form) {
        UsuarioModel novoFuncionario = new UsuarioModel(form, passwordEncoder.encode(form.getSenha()));
        return new FuncionarioDto(usuarioRepository.save(novoFuncionario));
    }

    @Override
    @Transactional
    public FuncionarioDto atualizarFuncionario(FuncionarioForm form, Long idFuncionario) {
        UsuarioModel usuario = this.alterarDados((this.getUsuarioPorId(idFuncionario)), form);
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
        // this.logsService.registrarLog(gerente, );
    }

    private void validarAtendente(Long idAtendented) {
        List<AtendimentoModel> atendimentosPendentes = this.atendimentoRepository.findAllByAtendente_IdAndStatus(idAtendented, StatusAtendimentoEnum.EM_ANDAMENTO);
        if (!atendimentosPendentes.isEmpty())
            throw new RuntimeException("O atendente ainda está participando de atendimentos!");
    }

    private void validarVeterinario(Long idVeterinario) {
        List<ConsultaModel> consultasPendentes = this.consultaRepository.findAllByVeterinario_Id(idVeterinario).stream().filter(consulta ->
                !consulta.getStatus().equals(StatusConsultaEnum.INICIADO) && !consulta.getStatus().equals(StatusConsultaEnum.PENDENTE)
        ).toList();
        if (!consultasPendentes.isEmpty())
            throw new RuntimeException("O veterinário possui consultas pendentes!");
    }

    private UsuarioModel alterarDados(UsuarioModel usuario, FuncionarioForm form) {
        usuario.setNome(!Objects.equals(usuario.getNome(), form.getNome()) ? form.getNome() : usuario.getNome());
        usuario.setSenha(!Objects.equals(usuario.getSenha(), passwordEncoder.encode(form.getSenha())) ? this.passwordEncoder.encode(form.getSenha()) : usuario.getSenha());
        usuario.setEmail(!Objects.equals(usuario.getEmail(), form.getEmail()) ? form.getEmail() : usuario.getEmail());
        usuario.setTelefone(!Objects.equals(usuario.getTelefone(), form.getTelefone()) ? form.getTelefone() : usuario.getTelefone());
        usuario.setPermissao(usuario.getPermissao() != form.getPermissao() ? form.getPermissao() : usuario.getPermissao());
        usuario.setGenero(usuario.getGenero() != form.getGenero() ? form.getGenero() : usuario.getGenero());
        usuario.setDataNascimento(usuario.getDataNascimento() != form.getDataNascimento() ? form.getDataNascimento() : usuario.getDataNascimento());
        usuario.setCpf(!Objects.equals(usuario.getCpf(), form.getCpf()) ? form.getCpf() : usuario.getCpf());
        return usuario;
    }
}
