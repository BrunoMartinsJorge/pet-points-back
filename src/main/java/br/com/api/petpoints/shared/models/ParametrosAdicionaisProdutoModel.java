package br.com.api.petpoints.shared.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "parametros_adicionais_produto")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParametrosAdicionaisProdutoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    private String nome;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private ProdutoModel produto;
}
