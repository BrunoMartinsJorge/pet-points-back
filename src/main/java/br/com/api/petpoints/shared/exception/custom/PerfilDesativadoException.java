package br.com.api.petpoints.shared.exception.custom;

public class PerfilDesativadoException extends RuntimeException {
    public PerfilDesativadoException(String mensagem) {
        super(mensagem);
    }
}
