package br.com.api.petpoints.modules.users.estoquista.features.estoque.dto;

import br.com.api.petpoints.modules.users.estoquista.shared.dto.MovimentacaoDto;
import br.com.api.petpoints.shared.enums.TipoProdutoEnum;
import br.com.api.petpoints.shared.models.MovimentacaoModel;
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
public class ProdutoDetalhesDto {
    private Long id;
    private String nome;
    private TipoProdutoEnum tipo;
    private String descricao;
    private double valorUnitario;
    private int quantidadeEstoque;
    private boolean quantidadeAbaixoEstoque;
    private List<MovimentacaoDto> historicoMovimentacoes;

    public ProdutoDetalhesDto(ProdutoModel produto, List<MovimentacaoModel> movimentacoes) {
        this.id = produto.getId();
        this.nome = produto.getNome();
        this.tipo = produto.getTipo();
        this.descricao = produto.getDescricao();
        this.valorUnitario = produto.getValorUnitario();
        this.quantidadeEstoque = produto.getQuantidadeEstoque();
        this.quantidadeAbaixoEstoque = produto.getQuantidadeEstoque() < produto.getQuantidadeMinima();
        this.historicoMovimentacoes = MovimentacaoDto.convert(movimentacoes);
    }
}

