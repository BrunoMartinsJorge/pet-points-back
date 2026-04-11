package br.com.api.petpoints.modules.users.estoquista.features.movimentacoes.dto;

import br.com.api.petpoints.shared.enums.TipoMovimentacaoEnum;
import br.com.api.petpoints.shared.models.MovimentacaoModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MinhasMovimentacoesDto {

    private Long id;
    private LocalDateTime dataHora;
    private TipoMovimentacaoEnum tipoMovimentacao;
    private int quantidadeMovimentada;
    private ProdutoMovimentacaoDto produto;

    public MinhasMovimentacoesDto(MovimentacaoModel movimentacao) {
        this.id = movimentacao.getId();
        this.dataHora = movimentacao.getMovimentadoEm();
        this.tipoMovimentacao = movimentacao.getTipo();
        this.quantidadeMovimentada = movimentacao.getQuantidadeMovimentada();
        this.produto = new ProdutoMovimentacaoDto(movimentacao.getProduto());
    }

    public static List<MinhasMovimentacoesDto> convert(List<MovimentacaoModel> movimentacoes) {
        return movimentacoes.stream().map(MinhasMovimentacoesDto::new).toList();
    }
}
