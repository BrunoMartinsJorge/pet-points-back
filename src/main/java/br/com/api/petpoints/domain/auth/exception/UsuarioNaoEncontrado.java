package br.com.api.petpoints.domain.auth.exception;

public class UsuarioNaoEncontrado extends RuntimeException{
    public UsuarioNaoEncontrado(String mensagem) {
        super(mensagem);
    }
    public UsuarioNaoEncontrado(Long idUsuario) {
        super("Usuário com ID: " + idUsuario + " não encontrado!");
    }
}
