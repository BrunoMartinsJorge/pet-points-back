package br.com.api.petpoints.modules.usuario.service;

import br.com.api.petpoints.modules.usuario.dto.TokenDto;
import br.com.api.petpoints.modules.usuario.forms.LoginForm;
import br.com.api.petpoints.modules.usuario.forms.RegistroForm;
import jakarta.mail.MessagingException;

public interface UsuarioService {
    TokenDto registrarUsuario(RegistroForm registroForm);
    TokenDto logarUsuario(LoginForm loginForm);
    void enviarCodigoAlteracaoSenha(String email) throws MessagingException;
    boolean validarCodigoAlterarSenha(String email, String codigoSenha);
    void alterarSenha(String email, String senha, String codigo);
}
