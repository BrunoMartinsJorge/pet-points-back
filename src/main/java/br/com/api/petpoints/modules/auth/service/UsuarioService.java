package br.com.api.petpoints.modules.auth.service;

import br.com.api.petpoints.modules.auth.dto.TokenDto;
import br.com.api.petpoints.modules.auth.forms.LoginForm;
import br.com.api.petpoints.modules.auth.forms.RegistroForm;
import jakarta.mail.MessagingException;

public interface UsuarioService {
    TokenDto registrarUsuario(RegistroForm registroForm);
    TokenDto logarUsuario(LoginForm loginForm);
    void enviarCodigoAlteracaoSenha(String email) throws MessagingException;
    boolean validarCodigoAlterarSenha(String email, String codigoSenha);
    void alterarSenha(String email, String senha, String codigo);
}
