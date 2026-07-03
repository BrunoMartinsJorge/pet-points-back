package br.com.api.petpoints.domain.users.estoquista.features.estoque.dto;

import br.com.api.petpoints.shared.models.ProdutoModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoRelatorioDto {

    private String nome;
    private String descricao;
    private double valorUnitario;
    private int quantidadeEstoque;
    private String tipo;
    private boolean abaixoEstoque;

    public ProdutoRelatorioDto(ProdutoModel produto) {
        this.nome = produto.getNome();
        this.descricao = produto.getDescricao();
        this.valorUnitario = produto.getValorUnitario();
        this.quantidadeEstoque = produto.getQuantidadeEstoque();
        this.tipo = produto.getTipo().getDescricao();
        this.abaixoEstoque = produto.getQuantidadeEstoque() < produto.getQuantidadeMinima();
    }

    public static List<ProdutoRelatorioDto> convert(List<ProdutoModel> produtos) {
        return produtos.stream().map(ProdutoRelatorioDto::new).toList();
    }
}
