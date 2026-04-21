package br.com.api.petpoints.core.security;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.core.token.TokenService;
import br.com.api.petpoints.modules.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.shared.exception.custom.TokenExpiradaException;
import br.com.api.petpoints.shared.exception.custom.TokenNaoEncontradaException;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final UsuarioRepository usuarioRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new TokenNaoEncontradaException("A token não foi encontrada ou é inválida!");
            }

            String token = authHeader.substring(7);

            if (!TokenService.tokenValida(token)) {
                throw new TokenExpiradaException("Token inválida ou expirada!");
            }

            TokenModel model = new TokenModel(token);

            UsuarioModel usuario = usuarioRepository.findById(model.getIdUsuario())
                    .orElseThrow(() -> new UsuarioNaoEncontrado("Usuário não encontrado!"));

            List<SimpleGrantedAuthority> authorities =
                    usuario.getAuthorities().stream()
                            .map(auth -> new SimpleGrantedAuthority(
                                    Objects.requireNonNull(auth.getAuthority())
                            ))
                            .toList();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            usuario,
                            null,
                            authorities
                    );

            accessor.setUser(authentication);
        }

        return message;
    }
}

