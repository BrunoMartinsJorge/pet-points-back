package br.com.api.petpoints.shared.models;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.enums.StatusPerfil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuario")
public class UsuarioModel implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull(message = "O campo 'email' não pode ser nulo!")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$\n" +
            "\n", message = "Campo 'email' com valor inválido para email!")
    private String email;

    @NotNull(message = "O campo 'senha' não pode ser nulo!")
    private String senha;

    @NotNull(message = "O campo 'nome' não pode ser nulo!")
    private String nome;

    @Column(unique = true)
    @NotNull(message = "O campo 'cpf' não pode ser nulo!")
    @Pattern(regexp = "[0-9]{3}\\\\.[0-9]{3}\\\\.[0-9]{3}-[0-9]{2}", message = "O campo 'CPF' está com um valor inválido para um CPF!")
    private String cpf;

    @NotNull(message = "O campo 'telefone' não pode ser nulo!")
    private String telefone;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "O campo 'genero' não pode ser nulo!")
    private GeneroEnum genero;

    @NotNull(message = "O campo 'data_nascimento' não pode ser nulo!")
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @CreationTimestamp
    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    private UUID imagem;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario")
    private TipoUsuario permissao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_conta")
    private StatusPerfil statusPerfil = StatusPerfil.A;

    public UsuarioModel(String email, String senha, TipoUsuario permissao) {
        this.email = email;
        this.senha = senha;
        this.permissao = permissao;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("RULE_REST_" + permissao.getDescricao()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}