package br.com.api.petpoints.modules.users.estoquista.shared.dto;

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
public class MovimentacaoDto {

    private Long id;
    private TipoMovimentacaoEnum tipoMovimentacao;
    private String produto;
    private LocalDateTime dataHoraMovimentacao;
    private int quantidadeMovimentada;

    public MovimentacaoDto(MovimentacaoModel movimentacao) {
        this.id = movimentacao.getId();
        this.tipoMovimentacao = movimentacao.getTipo();
        this.produto = movimentacao.getProduto().getNome();
        this.dataHoraMovimentacao = movimentacao.getMovimentadoEm();
        this.quantidadeMovimentada = movimentacao.getQuantidadeMovimentada();
    }

    public static List<MovimentacaoDto> convert(List<MovimentacaoModel> movimentacoes) {
        return movimentacoes.stream().map(MovimentacaoDto::new).toList();
    }
}
