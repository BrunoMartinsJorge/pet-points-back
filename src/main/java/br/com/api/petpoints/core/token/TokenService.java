package br.com.api.petpoints.core.token;

import br.com.api.petpoints.modules.usuario.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.modules.usuario.models.UsuarioModel;
import br.com.api.petpoints.modules.usuario.repository.UsuarioRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String gerarToken(UsuarioModel usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("vitapet-api")
                    .withSubject(usuario.getEmail())
                    .withClaim("id_usuario", usuario.getId())
                    .withClaim("nomeUsuario", usuario.getNome())
                    .withClaim("permissao", usuario.getPermissao().getDescricao())
                    .withExpiresAt(geradorDeDataDeExpiracao())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("Erro na hora da geração da token!", e);
        }
    }

    private Instant geradorDeDataDeExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

    @Service
    public static class AuthorizationService implements UserDetailsService {

        private final UsuarioRepository usuarioReporitory;

        public AuthorizationService(UsuarioRepository usuarioReporitory) {
            this.usuarioReporitory = usuarioReporitory;
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            return usuarioReporitory.findByEmail(username)
                    .orElseThrow(() -> new UsuarioNaoEncontrado("Usuário não encontrado com email: " + username));
        }
    }

    public static DecodedJWT converterToken(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token ausente ou vazio");
        }

        token = token.replace("Bearer", "").trim();

        try {
            return JWT.decode(token);
        } catch (Exception e) {
            throw new IllegalArgumentException("Token inválido: " + e.getMessage(), e);
        }
    }

    public static TokenModel gerarTokenModel(DecodedJWT token_decodificada) {
        return new TokenModel(
                token_decodificada.getToken(),
                token_decodificada.getClaim("id_usuario").asLong(),
                token_decodificada.getClaim("permissoes").asArray(String.class)[0],
                token_decodificada.getClaim("acessosPermitidos").asList(String.class),
                token_decodificada.getClaim("nomeUsuario").asString()
        );
    }

    public static TokenModel converterTokenParaModel(String token) {
        DecodedJWT token_decodificada = JWT.decode(token);
        return new TokenModel(
                token,
                token_decodificada.getClaim("id_usuario").asLong(),
                token_decodificada.getClaim("permissoes").asArray(String.class)[0],
                token_decodificada.getClaim("acessosPermitidos").asList(String.class),
                token_decodificada.getClaim("nomeUsuario").asString()
        );
    }
}
