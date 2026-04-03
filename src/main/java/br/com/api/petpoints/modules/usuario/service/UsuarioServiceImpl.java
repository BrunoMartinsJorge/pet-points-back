package br.com.api.petpoints.modules.usuario.service;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.core.token.TokenService;
import br.com.api.petpoints.modules.usuario.dto.TokenDto;
import br.com.api.petpoints.modules.usuario.exception.UsuarioJaCadastrado;
import br.com.api.petpoints.modules.usuario.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.modules.usuario.forms.LoginForm;
import br.com.api.petpoints.modules.usuario.forms.RegistroForm;
import br.com.api.petpoints.modules.usuario.model.TokenRecuperarSenhaModel;
import br.com.api.petpoints.modules.usuario.repository.TokenRecuperarSenhaRepository;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import br.com.api.petpoints.shared.enums.TipoLogEnum;
import br.com.api.petpoints.shared.features.logs.LogsServiceImpl;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final LogsServiceImpl logsService;
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSenderImpl mailSender;
    private final TokenRecuperarSenhaRepository tokenRecuperarSenhaRepository;

    private UsuarioModel getUsuarioPorId(Long idUsuario) {
        return this.usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: " + idUsuario + " não encontrado!"));
    }

    private UsuarioModel getUsuarioPorEmail(String email) {
        return this.usuarioRepository.findByEmail(email).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com Email: " + email + " não encontrado!"));
    }

    @Override
    public TokenDto registrarUsuario(RegistroForm registroForm) {
        if (usuarioRepository.existsByEmailOrCpf(registroForm.getEmail(), registroForm.getCpf()))
            throw new UsuarioJaCadastrado("Usuário já cadastrado!");
        UsuarioModel usuario = new UsuarioModel(registroForm, TipoUsuario.C, passwordEncoder.encode(registroForm.getSenha()));
        usuario = usuarioRepository.save(usuario);
        return new TokenDto(
                this.tokenService.gerarToken(usuario)
        );
    }

    @Override
    public TokenDto logarUsuario(LoginForm loginForm) {
        if (!usuarioRepository.existsByEmail(loginForm.getEmail()))
            throw new UsuarioNaoEncontrado("Usuário não encontrado!");
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginForm.getEmail(), loginForm.getSenha())
        );
        String token = tokenService.gerarToken((UsuarioModel) Objects.requireNonNull(auth.getPrincipal()));
        UsuarioModel usuario = (UsuarioModel) auth.getPrincipal();
        logsService.registrarLog(
                usuario,
                TipoLogEnum.LOGIN
        );
        return new TokenDto(
                token
        );
    }

    @Override
    public void enviarCodigoAlteracaoSenha(String email) {
        UsuarioModel usuario = this.getUsuarioPorEmail(email);
        String codigo = this.gerarCodigoAlfanumerico();
        try {
            Context context = new Context();
            context.setVariable("codigo", codigo);
            context.setVariable("usuario", usuario.getNome());
            String htmlTemplate = templateEngine.process("email/codigo_alterar_senha", context);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Código de Alteração de Senha");
            helper.setText(htmlTemplate, true);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao enviar email");
        } finally {
            TokenRecuperarSenhaModel token = new TokenRecuperarSenhaModel();
            token.setUsuarioModel(usuario);
            token.setExpires_at(LocalDateTime.now().plusMinutes(5));
            token.setToken(codigo);
            this.tokenRecuperarSenhaRepository.save(token);
        }
    }

    @Override
    public boolean validarCodigoAlterarSenha(String email, String codigoSenha) {
        Long idUsuario = this.getUsuarioPorEmail(email).getId();
        TokenRecuperarSenhaModel tokenRecuperarSenha = this.tokenRecuperarSenhaRepository.findByUsuarioModel_IdAndToken(idUsuario, codigoSenha).orElseThrow(() -> new ObjectNotFoundException("Código de recuperação de senha não encontrado!"));
        if (tokenRecuperarSenha.getExpires_at().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Código expirado!");
        if (tokenRecuperarSenha.getUsed())
            throw new RuntimeException("Código já utilizado!");
        this.tokenRecuperarSenhaRepository.save(tokenRecuperarSenha);
        return true;
    }

    @Override
    public void alterarSenha(String email, String senha, String codigo) {
        UsuarioModel usuario = this.getUsuarioPorEmail(email);
        TokenRecuperarSenhaModel tokenRecuperarSenha = this.tokenRecuperarSenhaRepository.findByUsuarioModel_IdAndToken(usuario.getId(), codigo).orElseThrow(() -> new ObjectNotFoundException("Código de recuperação de senha não encontrado!"));
        if (tokenRecuperarSenha.getExpires_at().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Código expirado!");
        if (tokenRecuperarSenha.getUsed())
            throw new RuntimeException("Código já utilizado!");
        String senhaEncriptada = passwordEncoder.encode(senha);
        if (Objects.equals(senhaEncriptada, usuario.getSenha()))
            throw new RuntimeException("A nova senha não pode ser igual a anterior!");
        usuario.setSenha(passwordEncoder.encode(senha));
        this.usuarioRepository.save(usuario);
        tokenRecuperarSenha.setUsed(true);
        this.tokenRecuperarSenhaRepository.save(tokenRecuperarSenha);
    }

    private String gerarCodigoAlfanumerico() {
        final String ALFABETO = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        final SecureRandom random = new SecureRandom();
        StringBuilder codigo = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            codigo.append(ALFABETO.charAt(random.nextInt(ALFABETO.length())));
        }
        return codigo.toString();
    }
}
