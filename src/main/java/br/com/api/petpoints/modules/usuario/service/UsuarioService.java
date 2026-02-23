package br.com.api.petpoints.modules.usuario.service;

import br.com.api.petpoints.modules.usuario.forms.LoginForm;
import br.com.api.petpoints.modules.usuario.forms.RegistroForm;

public interface UsuarioService {
    public String registrarUsuario(RegistroForm registroForm);
    public String logarUsuario(LoginForm loginForm);
}
