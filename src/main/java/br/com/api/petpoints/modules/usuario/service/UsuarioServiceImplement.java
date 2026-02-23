package br.com.api.petpoints.modules.usuario.service;

import br.com.api.petpoints.core.token.TokenService;
import br.com.api.petpoints.modules.usuario.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.modules.usuario.forms.LoginForm;
import br.com.api.petpoints.modules.usuario.forms.RegistroForm;
import br.com.api.petpoints.modules.usuario.models.UsuarioModel;
import br.com.api.petpoints.modules.usuario.repository.UsuarioRepository;
import br.com.api.petpoints.shared.enums.TipoLogEnum;
import br.com.api.petpoints.shared.features.logs.LogsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImplement implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final LogsServiceImpl logsService;

    @Override
    public String registrarUsuario(RegistroForm registroForm) {

        return "";
    }

    @Override
    public String logarUsuario(LoginForm loginForm) {
        if (!this.usuarioRepository.existsByEmailAndPassword(loginForm.getEmail(), passwordEncoder.encode(loginForm.getSenha()))) throw new UsuarioNaoEncontrado("Usuário não encontrado!");
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginForm.getEmail(), loginForm.getSenha())
        );
        this.logsService.registrarLog(((UsuarioModel) auth.getPrincipal()), TipoLogEnum.LOGIN);
        return tokenService.gerarToken((UsuarioModel) Objects.requireNonNull(auth.getPrincipal()));
    }
}
