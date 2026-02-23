package br.com.api.petpoints.core.security;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.core.token.TokenService;
import br.com.api.petpoints.modules.usuario.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.modules.usuario.models.UsuarioModel;
import br.com.api.petpoints.modules.usuario.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private UsuarioRepository usuarioReporitory;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        List<String> publicEndpoints = List.of(
                "/conta/login",
                "/conta/register",
                "/conta/recover-passowrd",
                "/conta/recover",
                "/conta/set-new-passowrd"
        );
        if (path.startsWith("/ws") || publicEndpoints.contains(path) || path.startsWith("/arquivos")) {
            filterChain.doFilter(request, response);
            return;
        }
        var token = this.recoverToken(request);
        if (token == null)
            throw new AuthenticationCredentialsNotFoundException("Token não encontrado!");
        TokenModel tokenDados = TokenService.converterTokenParaModel(token);
        Optional<UsuarioModel> user = this.usuarioReporitory.findById(tokenDados.getIdUsuario());
        if (user.isEmpty())
            throw new UsuarioNaoEncontrado("Usuário com ID: " + tokenDados.getIdUsuario() + " não encontrado!");
        var authentication = new UsernamePasswordAuthenticationToken(user, null, user.get().getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return authHeader.substring(7);
    }

}
