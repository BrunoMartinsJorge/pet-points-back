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
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthenticationEntryPoint entryPoint) throws Exception {

        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize

                        // LIBERE O WEBSOCKET AQUI!!!
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/ws/cliente/**").permitAll()
                        .requestMatchers("/ws/atendente/**").permitAll()
                        //.requestMatchers("/ws/notifications/**").permitAll()

                        // Endpoints de usuários abaixo:
                        .requestMatchers("/conta/**").permitAll()
                        .requestMatchers("/arquivos/**").permitAll()

                        // Endpoints de clientes abaixo:
                        .requestMatchers("/cliente/**").hasAuthority("RULE_REST_CLIENTE")

                        // Endpoints de atendentes abaixo:
                        .requestMatchers("/atendentes/**").hasAuthority("RULE_REST_ATENDENTE")

                        // Endpoints de gerentes abaixo:
                        .requestMatchers("/gerente/**").hasAuthority("RULE_REST_GERENTE")

                        .requestMatchers("/chat-interno/**").hasAnyAuthority("RULE_REST_ATENDENTE", "RULE_REST_DOUTORES", "RULE_REST_GERENTE")
                        .requestMatchers("/notifications/**").hasAnyAuthority("RULE_REST_ATENDENTE", "RULE_REST_DOUTORES", "RULE_REST_GERENTE")
                        .requestMatchers("/ws/notifications/**").hasAnyAuthority("RULE_REST_ATENDENTE", "RULE_REST_DOUTORES", "RULE_REST_GERENTE")

                        .requestMatchers("/relatorios/**").hasAnyAuthority("RULE_REST_GERENTE")

                        /*// Endpoints de usuários abaixo:
                        .requestMatchers(HttpMethod.POST, "/conta/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/arquivos/upload").permitAll()
                        .requestMatchers(HttpMethod.POST, "/conta/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/conta/recover-passowrd").permitAll()
                        .requestMatchers(HttpMethod.POST, "/conta/recover").permitAll()
                        .requestMatchers(HttpMethod.POST, "/conta/set-new-passowrd").permitAll()
                        .requestMatchers(HttpMethod.POST, "/conta/excluir-conta/").hasAnyRole("CLIENTE", "GERENTE", "FUNCIONARIO")

                        // Endpoints de clientes abaixo:
                        .requestMatchers(HttpMethod.POST, "/cliente/").hasAuthority("RULE_REST_CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/cliente/cadastrar-pet/").hasAuthority("RULE_REST_CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/cliente/chat/new-chat").hasAuthority("RULE_REST_CLIENTE")*/

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
