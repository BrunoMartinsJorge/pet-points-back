package br.com.api.petpoints.domain.users.gerente.features.financeiro.form;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class RelatorioFinanceiroForm {

    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String tipoPagamento;
    private String statusPagamento;
}
