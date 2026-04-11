package br.com.api.petpoints.core.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ConfigSecurity {

    private final SecurityFilter securityFilter;

    public ConfigSecurity(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {

        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize

                        .requestMatchers("/autenticacao/**").permitAll()

                        .requestMatchers("/ws/chat-interno/**").hasAnyAuthority("RULE_REST_ATENDENTE", "RULE_REST_GERENTE", "RULE_REST_DOUTORES")
                        .requestMatchers("/ws/notifications/**").hasAnyAuthority("RULE_REST_ATENDENTE", "RULE_REST_GERENTE", "RULE_REST_DOUTORES", "RULE_REST_CLIENTE")
                        .requestMatchers("/ws/chat-atendimento/**").hasAnyAuthority("RULE_REST_ATENDENTE", "RULE_REST_CLIENTE")

                        /*.requestMatchers("/arquivos/**").permitAll()*/

                        .requestMatchers("/cliente/**").hasAuthority("RULE_REST_CLIENTE")

                        .requestMatchers("/atendentes/**").hasAuthority("RULE_REST_ATENDENTE")

                        .requestMatchers("/gerente/**").hasAuthority("RULE_REST_GERENTE")

                        .requestMatchers("/estoquista/**").hasAuthority("RULE_REST_ESTOQUISTA")

                        .requestMatchers("/relatorios/**").hasAnyAuthority("RULE_REST_GERENTE")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
