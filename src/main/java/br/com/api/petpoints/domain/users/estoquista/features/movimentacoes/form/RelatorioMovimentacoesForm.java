package br.com.api.petpoints.domain.users.estoquista.features.movimentacoes.form;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RelatorioMovimentacoesForm {

    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private String tipoMovimentacao;
    private Long idProduto;
}
