package br.com.api.petpoints.shared.exception.custom;

public class TokenExpiradaException extends RuntimeException{
    public TokenExpiradaException(String message) {
        super(message);
    }
}
