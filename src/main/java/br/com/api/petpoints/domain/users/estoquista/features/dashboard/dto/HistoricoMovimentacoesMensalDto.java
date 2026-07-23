package br.com.api.petpoints.domain.users.estoquista.features.dashboard.dto;

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
public class HistoricoMovimentacoesMensalDto {
    private TipoMovimentacaoEnum tipoMovimentacao;
    private LocalDateTime dataHora;

    public HistoricoMovimentacoesMensalDto(MovimentacaoModel movimentacao) {
        this.tipoMovimentacao = movimentacao.getTipo();
        this.dataHora = movimentacao.getMovimentadoEm();
    }

    public static List<HistoricoMovimentacoesMensalDto> convert(List<MovimentacaoModel> movimentacoes) {
        return movimentacoes.stream().map(HistoricoMovimentacoesMensalDto::new).toList();
    }
}
