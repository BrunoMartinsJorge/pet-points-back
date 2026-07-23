package br.com.api.petpoints.shared.dto;

import br.com.api.petpoints.shared.models.PetModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CarteirinhaDto {

    private Long id;
    private String nome;
    private String genero;
    private String tipo;
    private String raca;
    private String nomeTutor;
    private String generoTutor;
    private String telefoneTutor;
    private String emailTutor;
    private String cpfTutor;
    private UUID imagem;

    public CarteirinhaDto(PetModel pet) {
        this.id = pet.getId();
        this.nome = pet.getNome();
        this.tipo = pet.getTipo().getDescricao();
        this.genero = pet.getGenero().getDescricao();
        this.raca = pet.getRaca();
        this.nomeTutor = pet.getTutor().getNome();
        this.generoTutor = pet.getTutor().getGenero().getDescricao();
        this.telefoneTutor = pet.getTutor().getTelefone();
        this.emailTutor = pet.getTutor().getEmail();
        this.cpfTutor = pet.getTutor().getCpf();
        this.imagem = pet.getImagem();
    }
}
