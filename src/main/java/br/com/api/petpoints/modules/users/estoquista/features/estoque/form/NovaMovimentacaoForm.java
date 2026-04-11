package br.com.api.petpoints.modules.users.estoquista.features.estoque.form;

import br.com.api.petpoints.shared.enums.TipoMovimentacaoEnum;
import lombok.Getter;

@Getter
public class NovaMovimentacaoForm {

    private Long idProduto;
    private int quantidadeMovimentada;
    private TipoMovimentacaoEnum tipoMovimentacao;
}
