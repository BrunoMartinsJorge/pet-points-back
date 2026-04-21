package br.com.api.petpoints.modules.users.gerente.features.estoque.dto;

import br.com.api.petpoints.shared.enums.TipoProdutoEnum;
import br.com.api.petpoints.shared.models.ProdutoModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoDto {

    private Long id;
    private String nome;
    private String descricao;
    private TipoProdutoEnum tipo;
    private double valorUnitario;
    private int quantidadeEstoque;
    private int quantidadeMinima;
    private boolean abaixoEstoque;

    public ProdutoDto(ProdutoModel produto) {
        this.id = produto.getId();
        this.nome = produto.getNome();
        this.descricao = produto.getDescricao();
        this.tipo = produto.getTipo();
        this.valorUnitario = produto.getValorUnitario();
        this.quantidadeEstoque = produto.getQuantidadeEstoque();
        this.quantidadeMinima = produto.getQuantidadeMinima();
        this.abaixoEstoque = quantidadeEstoque < quantidadeMinima;
    }
}
