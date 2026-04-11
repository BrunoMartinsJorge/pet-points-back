package br.com.api.petpoints.modules.auth.exception;

public class UsuarioNaoEncontrado extends RuntimeException{
    public UsuarioNaoEncontrado(String mensagem) {
        super(mensagem);
    }
}
