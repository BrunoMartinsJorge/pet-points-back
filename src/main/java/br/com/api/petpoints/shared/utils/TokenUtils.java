package br.com.api.petpoints.shared.utils;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.shared.exception.custom.TokenNaoEncontradaException;
import jakarta.servlet.http.HttpServletRequest;

public class TokenUtils {

    public static Long getIdUsuario(HttpServletRequest request) {
        return getToken(request).getIdUsuario();
    }

    public static TokenModel getToken(HttpServletRequest request) {
        if (request.getHeader("Authorization") == null) throw new TokenNaoEncontradaException("Token não encontrado!");
        return new TokenModel(request.getHeader("Authorization"));
    }
}
