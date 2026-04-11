package br.com.api.petpoints.modules.auth.exception;

public class UsuarioJaCadastrado extends RuntimeException {
    public UsuarioJaCadastrado(String mensagem) {
        super(mensagem);
    }
}
