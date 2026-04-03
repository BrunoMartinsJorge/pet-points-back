package br.com.api.petpoints.shared.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "arquivos")
public class ArquivosModel {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "O campo 'nome' não pode estar em branco!")
    private String nome;

    private String tipo;

    private Long descricao;

    @Lob
    @Column(columnDefinition = "bytea")
    private byte[] conteudo;
}
