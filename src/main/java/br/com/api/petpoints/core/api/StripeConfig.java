package br.com.api.petpoints.core.api;

import com.stripe.StripeClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StripeProperties.class)
public class StripeConfig {

    @Value("${stripe.secret-key}")
    private String apiKey;

    @Bean
    public StripeClient stripeClient() {
        return new StripeClient(apiKey);
    }
}
