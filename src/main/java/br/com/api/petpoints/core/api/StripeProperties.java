package br.com.api.petpoints.core.api;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "stripe")
public class StripeProperties {

    private String secretKey;
    private String publishableKey;
    private String webhookSecret;
    private String successUrl;
    private String cancelUrl;
}
