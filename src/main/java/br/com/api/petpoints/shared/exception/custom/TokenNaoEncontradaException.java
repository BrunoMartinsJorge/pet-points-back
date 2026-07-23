package br.com.api.petpoints.shared.exception.custom;

public class TokenNaoEncontradaException extends RuntimeException {
    public TokenNaoEncontradaException(String msg) {
        super(msg);
    }
}
