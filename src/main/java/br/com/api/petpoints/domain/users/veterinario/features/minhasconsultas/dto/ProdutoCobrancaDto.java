package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto;

import br.com.api.petpoints.shared.enums.TipoProdutoEnum;
import br.com.api.petpoints.shared.models.ProdutoModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Produto disponível para o veterinário lançar como item de cobrança
 * ao finalizar uma consulta.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoCobrancaDto {

    private Long id;
    private String nome;
    private TipoProdutoEnum tipo;
    private String descricao;
    private double valorUnitario;
    private int quantidadeEstoque;

    public ProdutoCobrancaDto(ProdutoModel produto) {
        this.id = produto.getId();
        this.nome = produto.getNome();
        this.tipo = produto.getTipo();
        this.descricao = produto.getDescricao();
        this.valorUnitario = produto.getValorUnitario();
        this.quantidadeEstoque = produto.getQuantidadeEstoque();
    }

    public static List<ProdutoCobrancaDto> convert(List<ProdutoModel> produtos) {
        return produtos.stream().map(ProdutoCobrancaDto::new).toList();
    }
}
