package br.com.api.petpoints.modules.users.gerente.features.funcionarios.dto;

import br.com.api.petpoints.shared.enums.TipoMovimentacaoEnum;
import br.com.api.petpoints.shared.models.MovimentacaoModel;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovimentacoesEstoquistasDto {

    private Long id;
    private String dataMovimentacao;
    private TipoMovimentacaoEnum tipo;
    private String produto;
    private int quantidade;

    public MovimentacoesEstoquistasDto(MovimentacaoModel movimentacao) {
        this.id = movimentacao.getId();
        this.dataMovimentacao = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(movimentacao.getMovimentadoEm());
        this.tipo = movimentacao.getTipo();
        this.produto = movimentacao.getProduto().getNome();
        this.quantidade = movimentacao.getQuantidadeMovimentada();
    }

    public static List<MovimentacoesEstoquistasDto> convert(List<MovimentacaoModel> movimentacoes) {
        return movimentacoes.stream().map(MovimentacoesEstoquistasDto::new).toList();
    }
}
