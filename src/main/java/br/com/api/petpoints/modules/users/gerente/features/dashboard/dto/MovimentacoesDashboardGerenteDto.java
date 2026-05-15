package br.com.api.petpoints.modules.users.gerente.features.dashboard.dto;

import br.com.api.petpoints.shared.enums.TipoMovimentacaoEnum;
import br.com.api.petpoints.shared.models.MovimentacaoModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovimentacoesDashboardGerenteDto {

    private TipoMovimentacaoEnum tipo;
    private String produto;
    private int quantidade;
    private String estoquista;

    public MovimentacoesDashboardGerenteDto(MovimentacaoModel movimentacao) {
        this.tipo = movimentacao.getTipo();
        this.produto = movimentacao.getProduto().getNome();
        this.quantidade = movimentacao.getQuantidadeMovimentada();
        this.estoquista = movimentacao.getMovimentadoPor().getNome();
    }

    public static List<MovimentacoesDashboardGerenteDto> convert(List<MovimentacaoModel> movimentacoes) {
        return movimentacoes.stream().map(MovimentacoesDashboardGerenteDto::new).toList();
    }
}
