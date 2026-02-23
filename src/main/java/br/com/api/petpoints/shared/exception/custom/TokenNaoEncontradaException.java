package br.com.api.petpoints.shared.exception.custom;

import javax.naming.AuthenticationException;

public class TokenNaoEncontradaException extends AuthenticationException {
    public TokenNaoEncontradaException(String msg) {
        super(msg);
    }
}
