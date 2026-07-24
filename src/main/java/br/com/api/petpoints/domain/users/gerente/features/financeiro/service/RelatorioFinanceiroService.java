package br.com.api.petpoints.domain.users.gerente.features.financeiro.service;

import br.com.api.petpoints.domain.users.gerente.features.financeiro.form.RelatorioFinanceiroForm;

public interface RelatorioFinanceiroService {
    byte[] gerarRelatorio(RelatorioFinanceiroForm form);
}
