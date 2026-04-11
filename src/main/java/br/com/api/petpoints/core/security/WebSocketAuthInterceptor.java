package br.com.api.petpoints.core.security;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.core.token.TokenService;
import br.com.api.petpoints.shared.exception.custom.TokenExpiradaException;
import br.com.api.petpoints.shared.exception.custom.TokenNaoEncontradaException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token == null)
                throw new TokenNaoEncontradaException("A token não foi encontrada!");
            if(!TokenService.tokenValida(token))
                throw new TokenExpiradaException("Token invalido!");
        }

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            TokenModel model = new TokenModel(token);
            accessor.setUser(() -> model.getIdUsuario().toString());
        }

        return message;
    }
}

