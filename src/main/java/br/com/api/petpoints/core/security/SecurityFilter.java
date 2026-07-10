package br.com.api.petpoints.core.security;

import br.com.api.petpoints.core.token.TokenService;
import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.shared.enums.StatusPerfilEnum;
import br.com.api.petpoints.shared.exception.custom.IllegalAccessException;
import br.com.api.petpoints.shared.exception.custom.TokenExpiradaException;
import br.com.api.petpoints.shared.exception.custom.TokenNaoEncontradaException;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private UsuarioRepository usuarioReporitory;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        List<String> publicEndpoints = List.of(
                "/autenticacao/login",
                "/autenticacao/register",
                "/autenticacao/enviar-codigo-alterar-senha",
                "/autenticacao/validar-codigo-alterar-senha",
                "/autenticacao/redefinir-senha"
        );
        if (path.contains("/ws") || publicEndpoints.contains(path) || path.startsWith("/arquivos") || path.contains("imagem")) {
            filterChain.doFilter(request, response);
            return;
        }
        var token = this.recoverToken(request);
        if (token == null)
            throw new TokenNaoEncontradaException("Token não encontrado!");
        if (!TokenService.tokenValida(token))
            throw new TokenExpiradaException("Token expirado!");
        UsernamePasswordAuthenticationToken authentication = this.validarUsuario(TokenService.converterTokenParaModel(token).getIdUsuario());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken validarUsuario(Long idUsuario) {
        UsuarioModel user = this.usuarioReporitory.findById(idUsuario).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: " + idUsuario + " não encontrado!"));
        if (user.getStatusPerfilEnum() == StatusPerfilEnum.D)
            throw new IllegalAccessException("Atividade de perfil desabilitado executada!");
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return authHeader.substring(7);
    }

}
