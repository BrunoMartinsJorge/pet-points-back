package br.com.api.petpoints.domain.usuario;

import br.com.api.petpoints.core.token.TokenService;
import br.com.api.petpoints.domain.auth.exception.UsuarioJaCadastrado;
import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.domain.auth.forms.LoginForm;
import br.com.api.petpoints.domain.auth.model.TokenRecuperarSenhaModel;
import br.com.api.petpoints.domain.auth.repository.TokenRecuperarSenhaRepository;
import br.com.api.petpoints.domain.auth.service.UsuarioServiceImpl;
import br.com.api.petpoints.shared.enums.StatusPerfilEnum;
import br.com.api.petpoints.shared.enums.TipoLogEnum;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.features.logs.LogsServiceImpl;
import br.com.api.petpoints.shared.form.RegistroForm;
import br.com.api.petpoints.shared.models.ArquivosModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.ArquivoRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do UsuarioServiceImpl")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private LogsServiceImpl logsService;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private JavaMailSenderImpl mailSender;

    @Mock
    private TokenRecuperarSenhaRepository tokenRecuperarSenhaRepository;

    @Mock
    private ArquivoRepository arquivoRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private UsuarioModel usuarioTeste;
    private RegistroForm registroForm;
    private LoginForm loginForm;

    @BeforeEach
    void setUp() {
        usuarioTeste = new UsuarioModel();
        usuarioTeste.setId(1L);
        usuarioTeste.setEmail("teste@email.com");
        usuarioTeste.setNome("Usuário Teste");
        usuarioTeste.setCpf("12345678900");
        usuarioTeste.setSenha("senhaEncriptada");
        usuarioTeste.setStatusPerfilEnum(StatusPerfilEnum.A);

        registroForm = new RegistroForm();
        registroForm.setEmail("novo@email.com");
        registroForm.setCpf("98765432100");
        registroForm.setNome("Novo Usuário");
        registroForm.setSenha("senha123");

        loginForm = new LoginForm();
        loginForm.setEmail("teste@email.com");
        loginForm.setSenha("senha123");
    }

    @Test
    @DisplayName("Deve registrar novo usuário com sucesso")
    void testRegistrarUsuarioComSucesso() {
        MultipartFile arquivo = mock(MultipartFile.class);
        when(arquivo.isEmpty()).thenReturn(true);
        when(usuarioRepository.existsByEmailOrCpf(anyString(), anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("senhaEncriptada");
        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(usuarioTeste);
        when(tokenService.gerarToken(any(UsuarioModel.class))).thenReturn("token123");

        var resultado = usuarioService.registrarUsuario(registroForm, arquivo);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getToken()).isEqualTo("token123");
        verify(usuarioRepository).save(any(UsuarioModel.class));
        verify(tokenService).gerarToken(any(UsuarioModel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário já cadastrado")
    void testRegistrarUsuarioJaCadastrado() {
        MultipartFile arquivo = mock(MultipartFile.class);
        when(usuarioRepository.existsByEmailOrCpf(anyString(), anyString())).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.registrarUsuario(registroForm, arquivo))
                .isInstanceOf(UsuarioJaCadastrado.class)
                .hasMessage("Usuário já cadastrado!");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve salvar usuário com arquivo de imagem")
    void testRegistrarUsuarioComImagem() throws IOException {
        MultipartFile arquivo = mock(MultipartFile.class);
        UUID imagemId = UUID.randomUUID();

        when(arquivo.isEmpty()).thenReturn(false);
        when(arquivo.getSize()).thenReturn(1_000_000L);
        when(arquivo.getContentType()).thenReturn("image/jpeg");
        when(arquivo.getOriginalFilename()).thenReturn("foto.jpg");
        when(arquivo.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(usuarioRepository.existsByEmailOrCpf(anyString(), anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("senhaEncriptada");

        ArquivosModel arquivoSalvo = new ArquivosModel();
        arquivoSalvo.setId(imagemId);
        when(arquivoRepository.save(any(ArquivosModel.class))).thenReturn(arquivoSalvo);
        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(usuarioTeste);
        when(tokenService.gerarToken(any(UsuarioModel.class))).thenReturn("token123");

        var resultado = usuarioService.registrarUsuario(registroForm, arquivo);

        assertThat(resultado.getToken()).isEqualTo("token123");
        verify(arquivoRepository).save(any(ArquivosModel.class));
    }

    @Test
    @DisplayName("Deve fazer login com sucesso")
    void testLogarUsuarioComSucesso() {
        Authentication authentication = mock(Authentication.class);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuarioTeste);
        when(tokenService.gerarToken(any(UsuarioModel.class))).thenReturn("token123");

        var resultado = usuarioService.logarUsuario(loginForm);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getToken()).isEqualTo("token123");
        verify(logsService).registrarLog(usuarioTeste, TipoLogEnum.LOGIN);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado no login")
    void testLogarUsuarioNaoEncontrado() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.logarUsuario(loginForm))
                .isInstanceOf(UsuarioNaoEncontrado.class)
                .hasMessage("Usuário não encontrado!");
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando perfil está desabilitado")
    void testLogarUsuarioPerfilDesabilitado() {
        usuarioTeste.setStatusPerfilEnum(StatusPerfilEnum.D);
        Authentication authentication = mock(Authentication.class);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuarioTeste);

        assertThatThrownBy(() -> usuarioService.logarUsuario(loginForm))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Seu perfil foi desabilitado. Por favor solicite por email uma reativação!");
        verify(logsService, never()).registrarLog(any(), any());
    }

    @Test
    @DisplayName("Deve enviar código de alteração de senha com sucesso")
    void testEnviarCodigoAlteracaoSenhaComSucesso() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuarioTeste));
        when(templateEngine.process(anyString(), any())).thenReturn("<html>Código</html>");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(tokenRecuperarSenhaRepository.save(any(TokenRecuperarSenhaModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.enviarCodigoAlteracaoSenha("teste@email.com");

        verify(usuarioRepository).findByEmail("teste@email.com");
        verify(mailSender).send(any(MimeMessage.class));
        verify(tokenRecuperarSenhaRepository).save(any(TokenRecuperarSenhaModel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado no envio de código")
    void testEnviarCodigoUsuarioNaoEncontrado() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.enviarCodigoAlteracaoSenha("inexistente@email.com"))
                .isInstanceOf(UsuarioNaoEncontrado.class);
        verify(mailSender, never()).send((SimpleMailMessage) any());
    }

    @Test
    @DisplayName("Deve validar código de alteração de senha com sucesso")
    void testValidarCodigoAlterarSenhaComSucesso() {
        TokenRecuperarSenhaModel token = new TokenRecuperarSenhaModel();
        token.setToken("ABC123");
        token.setExpires_at(LocalDateTime.now().plusMinutes(5));
        token.setUsed(false);

        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuarioTeste));
        when(tokenRecuperarSenhaRepository.findByUsuarioModel_IdAndToken(anyLong(), anyString()))
                .thenReturn(Optional.of(token));
        when(tokenRecuperarSenhaRepository.save(any(TokenRecuperarSenhaModel.class)))
                .thenReturn(token);

        boolean resultado = usuarioService.validarCodigoAlterarSenha("teste@email.com", "ABC123");

        assertThat(resultado).isTrue();
        verify(tokenRecuperarSenhaRepository).save(token);
    }

    @Test
    @DisplayName("Deve lançar exceção quando código está expirado")
    void testValidarCodigoExpirado() {
        TokenRecuperarSenhaModel token = new TokenRecuperarSenhaModel();
        token.setToken("ABC123");
        token.setExpires_at(LocalDateTime.now().minusMinutes(5));
        token.setUsed(false);

        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuarioTeste));
        when(tokenRecuperarSenhaRepository.findByUsuarioModel_IdAndToken(anyLong(), anyString()))
                .thenReturn(Optional.of(token));

        assertThatThrownBy(() -> usuarioService.validarCodigoAlterarSenha("teste@email.com", "ABC123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Código expirado!");
    }

    @Test
    @DisplayName("Deve lançar exceção quando código já foi utilizado")
    void testValidarCodigoJaUtilizado() {
        TokenRecuperarSenhaModel token = new TokenRecuperarSenhaModel();
        token.setToken("ABC123");
        token.setExpires_at(LocalDateTime.now().plusMinutes(5));
        token.setUsed(true);

        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuarioTeste));
        when(tokenRecuperarSenhaRepository.findByUsuarioModel_IdAndToken(anyLong(), anyString()))
                .thenReturn(Optional.of(token));

        assertThatThrownBy(() -> usuarioService.validarCodigoAlterarSenha("teste@email.com", "ABC123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Código já utilizado!");
    }

    @Test
    @DisplayName("Deve alterar senha com sucesso")
    void testAlterarSenhaComSucesso() {
        TokenRecuperarSenhaModel token = new TokenRecuperarSenhaModel();
        token.setToken("ABC123");
        token.setExpires_at(LocalDateTime.now().plusMinutes(5));
        token.setUsed(false);

        String novaSenhaEncriptada = "novaSenhaEncriptada";

        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuarioTeste));
        when(tokenRecuperarSenhaRepository.findByUsuarioModel_IdAndToken(anyLong(), anyString()))
                .thenReturn(Optional.of(token));
        when(passwordEncoder.encode("novaSenha123")).thenReturn(novaSenhaEncriptada);
        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(usuarioTeste);
        when(tokenRecuperarSenhaRepository.save(any(TokenRecuperarSenhaModel.class))).thenReturn(token);

        usuarioService.alterarSenha("teste@email.com", "novaSenha123", "ABC123");

        ArgumentCaptor<UsuarioModel> usuarioCaptor = ArgumentCaptor.forClass(UsuarioModel.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());

        ArgumentCaptor<TokenRecuperarSenhaModel> tokenCaptor = ArgumentCaptor.forClass(TokenRecuperarSenhaModel.class);
        verify(tokenRecuperarSenhaRepository).save(tokenCaptor.capture());

        assertThat(tokenCaptor.getValue().getUsed()).isTrue();
    }

    @Test
    @DisplayName("Deve lançar exceção quando nova senha é igual à anterior")
    void testAlterarSenhaIgualAnterior() {
        TokenRecuperarSenhaModel token = new TokenRecuperarSenhaModel();
        token.setToken("ABC123");
        token.setExpires_at(LocalDateTime.now().plusMinutes(5));
        token.setUsed(false);

        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuarioTeste));
        when(tokenRecuperarSenhaRepository.findByUsuarioModel_IdAndToken(anyLong(), anyString()))
                .thenReturn(Optional.of(token));
        when(passwordEncoder.encode("mesmoPassword")).thenReturn("senhaEncriptada");

        assertThatThrownBy(() -> usuarioService.alterarSenha("teste@email.com", "mesmoPassword", "ABC123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("A nova senha não pode ser igual a anterior!");
    }

    @Test
    @DisplayName("Deve buscar imagem do usuário com sucesso")
    void testBuscarImagemUsuarioComSucesso() {
        UUID imagemId = UUID.randomUUID();
        ArquivosModel arquivo = new ArquivosModel();
        arquivo.setId(imagemId);
        arquivo.setNome("foto.jpg");

        when(arquivoRepository.findById(imagemId)).thenReturn(Optional.of(arquivo));

        var resultado = usuarioService.buscarImagemUsuario(imagemId);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(imagemId);
        assertThat(resultado.getNome()).isEqualTo("foto.jpg");
    }

    @Test
    @DisplayName("Deve lançar exceção quando arquivo não encontrado")
    void testBuscarImagemNaoEncontrada() {
        UUID imagemId = UUID.randomUUID();
        when(arquivoRepository.findById(imagemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.buscarImagemUsuario(imagemId))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Arquivo não encontrado!");
    }

    @Test
    @DisplayName("Deve lançar exceção quando arquivo excede tamanho máximo")
    void testSalvarArquivoTamanhoExcedido() throws IOException {
        MultipartFile arquivo = mock(MultipartFile.class);
        when(arquivo.isEmpty()).thenReturn(false);
        when(arquivo.getSize()).thenReturn(6_000_000L);

        assertThatThrownBy(() -> usuarioService.registrarUsuario(registroForm, arquivo))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Arquivo passa de 5MB!");
    }

    @Test
    @DisplayName("Deve lançar exceção quando tipo de arquivo é inválido")
    void testSalvarArquivoTipoInvalido() throws IOException {
        MultipartFile arquivo = mock(MultipartFile.class);
        when(arquivo.isEmpty()).thenReturn(false);
        when(arquivo.getSize()).thenReturn(1_000_000L);
        when(arquivo.getContentType()).thenReturn("application/exe");

        assertThatThrownBy(() -> usuarioService.registrarUsuario(registroForm, arquivo))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Tipo inválido");
    }
}