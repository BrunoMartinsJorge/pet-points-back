package br.com.api.petpoints.shared.utils;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.shared.exception.custom.TokenNaoEncontradaException;
import br.com.api.petpoints.shared.models.UsuarioModel;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import java.security.Principal;

public class TokenUtils {

    public static Long getIdUsuario(HttpServletRequest request) {
        return getToken(request).getIdUsuario();
    }

    public static TokenModel getToken(HttpServletRequest request) {
        if (request.getHeader("Authorization") == null) throw new TokenNaoEncontradaException("Token não encontrado!");
        return new TokenModel(request.getHeader("Authorization"));
    }

    public static Long getIdUsuario(Principal principal) {
        if (principal instanceof Authentication auth && auth.getPrincipal() instanceof UsuarioModel usuario) {
            return usuario.getId();
        }
        throw new RuntimeException("Usuario nao autenticado no WebSocket!");
    }
}
