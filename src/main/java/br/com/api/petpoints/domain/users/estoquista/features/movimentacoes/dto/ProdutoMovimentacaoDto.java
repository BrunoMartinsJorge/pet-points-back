package br.com.api.petpoints.domain.users.estoquista.features.movimentacoes.dto;

import br.com.api.petpoints.shared.models.ProdutoModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoMovimentacaoDto {

    private Long id;
    private String nome;
    private String descricao;
    private double valorUnitario;

    public ProdutoMovimentacaoDto(ProdutoModel produto) {
        this.id = produto.getId();
        this.nome = produto.getNome();
        this.descricao = produto.getDescricao();
        this.valorUnitario = produto.getValorUnitario();
    }
}
