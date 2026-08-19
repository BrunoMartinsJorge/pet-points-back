package br.com.api.petpoints.domain.auth.service;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.core.token.TokenService;
import br.com.api.petpoints.domain.auth.dto.TokenDto;
import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.domain.auth.forms.LoginForm;
import br.com.api.petpoints.domain.auth.repository.TokenRecuperarSenhaRepository;
import br.com.api.petpoints.domain.auth.exception.UsuarioJaCadastrado;
import br.com.api.petpoints.shared.exception.custom.IllegalAccessException;
import br.com.api.petpoints.shared.form.RegistroForm;
import br.com.api.petpoints.domain.auth.model.TokenRecuperarSenhaModel;
import br.com.api.petpoints.shared.enums.StatusPerfilEnum;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.models.ArquivosModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.ArquivoRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import br.com.api.petpoints.shared.enums.TipoLogEnum;
import br.com.api.petpoints.shared.features.logs.LogsServiceImpl;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
    private final ArquivoRepository arquivoRepository;

    private UsuarioModel getUsuarioPorId(Long idUsuario) {
        return this.usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: " + idUsuario + " não encontrado!"));
    }

    private UsuarioModel getUsuarioPorEmail(String email) {
        return this.usuarioRepository.findByEmail(email).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com Email: " + email + " não encontrado!"));
    }

    /**
     * Registrar novo cliente no sistema.
     * <p>
     *     Recebe um formulário do front e com base nas informações dele gera um novo registro no banco,
     *     além de gerar um LOG para informar data/hora dessa ação.
     * </p>
     *
     * @param registroForm  dados do cliente a serem registrados
     * @param arquivo       {@code MultipartFile} para registrar a foto de usuário, e {@code null} para não adicionar
     * @return TokenDto     Token de login do usuário
     */
    @Override
    @Transactional
    public TokenDto registrarUsuario(RegistroForm registroForm, MultipartFile arquivo) {
        if (usuarioRepository.existsByEmailOrCpf(registroForm.getEmail(), registroForm.getCpf()))
            throw new UsuarioJaCadastrado("Usuário já cadastrado!");
        UsuarioModel usuario = new UsuarioModel(registroForm, TipoUsuario.C, passwordEncoder.encode(registroForm.getSenha()));
        if (!arquivo.isEmpty())
            usuario.setImagem(this.salvarArquivo(arquivo));
        usuario = usuarioRepository.save(usuario);
        return new TokenDto(
                this.tokenService.gerarToken(usuario)
        );
    }

    /**
     * Salva a foto de perfil do usuário
     * <p>
     *     Recebe dos métodos que o utilizam um {@code MultipartFile} para gerar registrar a foto de perfil.
     *     Verifica se o arquivo não passa dos 5MB definidos como tamanho máximo e se o tipo do arquivo consta nos permitidos.
     *     Retornando o identificador da imagem no banco de dados, sendo feita de um {@code UUID}.
     * </p>
     *
     * @param form   {@code MultipartFile} para registrar a foto de usuário, e {@code null} para não adicionar
     * @return UUID  Identificador da foto de perfil do cliente
     */
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

    /**
     * Realiza a autenticação do usuário.
     *
     * <p>O método recebe um {@code LoginForm} contendo o e-mail e a senha
     * informados pelo usuário e executa as seguintes etapas:</p>
     *
     * <ol>
     *     <li>Verifica se existe um usuário cadastrado com o e-mail informado;</li>
     *     <li>
     *         Caso o usuário não seja encontrado, lança
     *         {@code UsuarioNaoEncontrado};
     *     </li>
     *     <li>
     *         Realiza a autenticação utilizando o e-mail e a senha informados;
     *     </li>
     *     <li>
     *         Gera o token de autenticação para o usuário autenticado;
     *     </li>
     *     <li>
     *         Verifica se o perfil do usuário está desabilitado;
     *     </li>
     *     <li>
     *         Caso o perfil esteja desabilitado, lança
     *         {@code IllegalAccessException};
     *     </li>
     *     <li>Registra um log referente ao login realizado;</li>
     *     <li>Retorna o token de autenticação.</li>
     * </ol>
     *
     * @param loginForm formulário de login contendo o e-mail e a senha do usuário
     * @return {@code TokenDto} contendo o token de autenticação gerado
     * @throws UsuarioNaoEncontrado caso não exista usuário cadastrado com o e-mail informado
     * @throws IllegalAccessException caso o perfil do usuário esteja desabilitado
     */
    @Override
    public TokenDto logarUsuario(LoginForm loginForm) {
        if (!usuarioRepository.existsByEmail(loginForm.getEmail()))
            throw new UsuarioNaoEncontrado("Usuário não encontrado!");

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginForm.getEmail(),
                        loginForm.getSenha()
                )
        );

        String token = tokenService.gerarToken(
                (UsuarioModel) Objects.requireNonNull(auth.getPrincipal())
        );

        UsuarioModel usuario = (UsuarioModel) auth.getPrincipal();

        if (usuario.getStatusPerfilEnum().equals(StatusPerfilEnum.D))
            throw new IllegalAccessException(
                    "Seu perfil foi desabilitado. Por favor solicite por email uma reativação!"
            );

        logsService.registrarLog(
                usuario,
                TipoLogEnum.LOGIN
        );

        return new TokenDto(token);
    }
    /**
     * Envia um código de alteração de senha
     * <p>
     *     Recebe um email {@code String} do front para enviar um código de verificação de email
     *     Caso exista um usuário ele gera um 'código' alfanumérico e o envia
     * </p>
     *
     * @param email  {@code String} Email para envio de código de confirmação
     */
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

    @Override
    public ArquivosModel buscarImagemUsuario(UUID id) {
        return this.arquivoRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Arquivo não encontrado!"));
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
