package br.com.api.petpoints.domain.auth.service;

import br.com.api.petpoints.domain.auth.dto.TokenDto;
import br.com.api.petpoints.domain.auth.forms.LoginForm;
import br.com.api.petpoints.domain.auth.forms.RegistroForm;
import br.com.api.petpoints.shared.models.ArquivosModel;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UsuarioService {
    TokenDto registrarUsuario(RegistroForm registroForm, MultipartFile arquivo);
    TokenDto logarUsuario(LoginForm loginForm);
    void enviarCodigoAlteracaoSenha(String email) throws MessagingException;
    boolean validarCodigoAlterarSenha(String email, String codigoSenha);
    void alterarSenha(String email, String senha, String codigo);
    ArquivosModel buscarImagemUsuario(UUID id);
}
