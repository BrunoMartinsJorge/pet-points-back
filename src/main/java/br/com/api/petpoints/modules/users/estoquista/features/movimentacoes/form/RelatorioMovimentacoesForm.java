package br.com.api.petpoints.modules.users.estoquista.features.movimentacoes.form;

import br.com.api.petpoints.shared.enums.TipoMovimentacaoEnum;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RelatorioMovimentacoesForm {

    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private TipoMovimentacaoEnum tipoMovimentacao;
    private Long idProduto;
}
