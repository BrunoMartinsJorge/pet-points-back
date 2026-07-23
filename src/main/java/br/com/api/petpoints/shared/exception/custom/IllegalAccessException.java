package br.com.api.petpoints.shared.exception.custom;

public class IllegalAccessException extends RuntimeException {
    public IllegalAccessException(String message) {
        super(message);
    }

    public IllegalAccessException() {
        super("Você não tem acesso a isso!");
    }
}
