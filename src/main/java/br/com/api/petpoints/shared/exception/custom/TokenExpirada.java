package br.com.api.petpoints.shared.exception.custom;

public class TokenExpirada extends RuntimeException{
    public TokenExpirada(String message) {
        super(message);
    }
}
