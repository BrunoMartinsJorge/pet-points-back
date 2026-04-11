package br.com.api.petpoints.shared.models;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.modules.auth.forms.RegistroForm;
import br.com.api.petpoints.modules.users.gerente.features.funcionarios.forms.FuncionarioForm;
import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.enums.StatusPerfilEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.br.CPF;
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
    private String email;

    @NotNull(message = "O campo 'senha' não pode ser nulo!")
    @Column(name = "password")
    private String senha;

    @NotNull(message = "O campo 'nome' não pode ser nulo!")
    private String nome;

    @Column(unique = true)
    @NotNull(message = "O campo 'cpf' não pode ser nulo!")
    @CPF(message = "CPF inválido!")
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
    private StatusPerfilEnum statusPerfilEnum = StatusPerfilEnum.A;

    public UsuarioModel(String email, String senhaEncoded, TipoUsuario tipoUsuario) {
        this.email = email;
        this.senha = senhaEncoded;
        this.permissao = tipoUsuario;
    }

    public UsuarioModel(RegistroForm registro, TipoUsuario tipoUsuario, String senhaEncoded) {
        this.email = registro.getEmail();
        this.senha = senhaEncoded;
        this.permissao = tipoUsuario;
        this.cpf = registro.getCpf();
        this.dataNascimento = registro.getDataNascimento();
        this.genero = registro.getGenero();
        this.nome = registro.getNome();
        this.telefone = registro.getTelefone();
    }

    public UsuarioModel(FuncionarioForm form, String senhaEncoded) {
        this.email = form.getEmail();
        this.senha = senhaEncoded;
        this.permissao = form.getPermissao();
        this.cpf = form.getCpf();
        this.dataNascimento = form.getDataNascimento();
        this.genero = form.getGenero();
        this.nome = form.getNome();
        this.telefone = form.getTelefone();
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