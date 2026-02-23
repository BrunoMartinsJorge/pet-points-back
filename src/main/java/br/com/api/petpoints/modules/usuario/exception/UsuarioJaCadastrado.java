package br.com.api.petpoints.modules.usuario.exception;

public class UsuarioJaCadastrado extends RuntimeException {
    public UsuarioJaCadastrado(String mensagem) {
        super(mensagem);
    }
}
