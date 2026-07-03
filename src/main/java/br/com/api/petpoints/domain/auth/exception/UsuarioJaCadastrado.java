package br.com.api.petpoints.domain.auth.exception;

public class UsuarioJaCadastrado extends RuntimeException {
    public UsuarioJaCadastrado(String mensagem) {
        super(mensagem);
    }
}
