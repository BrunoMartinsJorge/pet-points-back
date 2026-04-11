package br.com.api.petpoints.shared.models;

import br.com.api.petpoints.modules.users.estoquista.features.estoque.form.NovoProdutoForm;
import br.com.api.petpoints.shared.enums.TipoProdutoEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "produto")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoProdutoEnum tipo;

    @Min(0)
    @Column(name = "valor_unitario")
    private double valorUnitario;

    @Min(0)
    @Column(name = "quantidade_estoque")
    private int quantidadeEstoque;

    @Min(0)
    @Column(name = "quantidade_minima")
    private int quantidadeMinima;

    private String descricao;

    private String nome;

    public ProdutoModel(NovoProdutoForm form) {
        this.tipo = form.getTipo();
        this.valorUnitario = form.getValorUnitario();
        this.quantidadeEstoque = form.getQuantidade();
        this.quantidadeMinima = form.getQuantidadeMinima();
        this.descricao = form.getDescricao();
        this.nome = form.getNome();
    }
}
