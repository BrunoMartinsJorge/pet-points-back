package br.com.api.petpoints.modules.users.gerente.features.movimentacoes.dto;

import br.com.api.petpoints.shared.dto.ProdutoFiltroDto;
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
public class MovimentacoesDto {

    private Long id;
    private LocalDateTime dataHora;
    private ProdutoFiltroDto produto;
    private TipoMovimentacaoEnum tipoMovimentacao;
    private LancadoPorDto lancadoPor;
    private int quantidadeMovimentada;

    public MovimentacoesDto(MovimentacaoModel movimentacao) {
        this.id = movimentacao.getId();
        this.dataHora = movimentacao.getMovimentadoEm();
        this.produto = new ProdutoFiltroDto(movimentacao.getProduto());
        this.tipoMovimentacao = movimentacao.getTipo();
        this.lancadoPor = new LancadoPorDto(movimentacao.getMovimentadoPor());
        this.quantidadeMovimentada = movimentacao.getQuantidadeMovimentada();
    }

    public static List<MovimentacoesDto> convert(List<MovimentacaoModel> movimentacoes) {
        return movimentacoes.stream().map(MovimentacoesDto::new).toList();
    }
}
