package br.com.api.petpoints.core.api;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mercado-pago")
public record MercadoPagoProperties(
        String baseUrl,
        String accessToken,
        String publicKey
) {
}
