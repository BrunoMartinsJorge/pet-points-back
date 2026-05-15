package br.com.api.petpoints.core.initializer;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.modules.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.models.EspecializacaoModel;
import br.com.api.petpoints.shared.models.TipoConsultaModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.EspecializacaoRepository;
import br.com.api.petpoints.shared.repository.TipoConsultaRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class UsuariosPadroes implements CommandLineRunner {
    private final UsuarioRepository usuarioRepository;
    private final EspecializacaoRepository especializacaoRepository;
    private final TipoConsultaRepository tipoConsultaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @NullMarked
    public void run(String... args) throws Exception {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setNome("Bruno Martins Jorge");
        usuario.setEmail("rexon300008@gmail.com");
        usuario.setSenha(this.passwordEncoder.encode("123456"));
        usuario.setGenero(GeneroEnum.M);
        usuario.setDataNascimento(LocalDate.parse("2006-06-12"));
        usuario.setTelefone("18996313182");
        usuario.setCpf("48250377044");
        usuario.setPermissao(TipoUsuario.G);
        if (!this.usuarioRepository.existsByEmailOrCpf(usuario.getEmail(), usuario.getCpf()))
            this.usuarioRepository.save(usuario);
        usuario.setNome("Carlos Eduardo Zaul");
        usuario.setEmail("usuario@gmail.com");
        usuario.setSenha(this.passwordEncoder.encode("123456"));
        usuario.setGenero(GeneroEnum.M);
        usuario.setDataNascimento(LocalDate.parse("2005-08-21"));
        usuario.setTelefone("18996413456");
        usuario.setCpf("22307089020");
        usuario.setPermissao(TipoUsuario.V);
        if (!this.usuarioRepository.existsByEmailOrCpf(usuario.getEmail(), usuario.getCpf()))
            this.usuarioRepository.save(usuario);
        usuario.setNome("Arthur Moura Rieger");
        usuario.setEmail("atendente@gmail.com");
        usuario.setSenha(this.passwordEncoder.encode("123456"));
        usuario.setGenero(GeneroEnum.M);
        usuario.setDataNascimento(LocalDate.parse("2006-11-26"));
        usuario.setTelefone("18996312459");
        usuario.setCpf("28449414008");
        usuario.setPermissao(TipoUsuario.A);
        if (!this.usuarioRepository.existsByEmailOrCpf(usuario.getEmail(), usuario.getCpf()))
            this.usuarioRepository.save(usuario);
        usuario.setNome("Marizinha");
        usuario.setEmail("cliente@gmail.com");
        usuario.setSenha(this.passwordEncoder.encode("123456"));
        usuario.setGenero(GeneroEnum.F);
        usuario.setDataNascimento(LocalDate.parse("1980-10-16"));
        usuario.setTelefone("18996738459");
        usuario.setCpf("60099673096");
        usuario.setPermissao(TipoUsuario.C);
        if (!this.usuarioRepository.existsByEmailOrCpf(usuario.getEmail(), usuario.getCpf()))
            this.usuarioRepository.save(usuario);
        usuario.setNome("Yann");
        usuario.setEmail("estoquista@gmail.com");
        usuario.setSenha(this.passwordEncoder.encode("123456"));
        usuario.setGenero(GeneroEnum.M);
        usuario.setDataNascimento(LocalDate.parse("2002-01-06"));
        usuario.setTelefone("18996897425");
        usuario.setCpf("24711763058");
        usuario.setPermissao(TipoUsuario.E);
        if (!this.usuarioRepository.existsByEmailOrCpf(usuario.getEmail(), usuario.getCpf()))
            this.usuarioRepository.save(usuario);
        UsuarioModel vet = this.usuarioRepository.findById(12L).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: " + 12 + " não encontrado!"));
    }

    private void limpar() {
        this.usuarioRepository.deleteAll(this.usuarioRepository.findAllByPermissao(TipoUsuario.A));
    }
}