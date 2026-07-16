package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto;

import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.models.UsuarioModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InformacoesClienteConsultaSelecionadaDto {

    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private GeneroEnum genero;
    private UUID imagem;

    public InformacoesClienteConsultaSelecionadaDto(UsuarioModel usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.cpf = usuario.getCpf();
        this.telefone = usuario.getTelefone();
        this.genero = usuario.getGenero();
        this.imagem = usuario.getImagem();
    }
}
