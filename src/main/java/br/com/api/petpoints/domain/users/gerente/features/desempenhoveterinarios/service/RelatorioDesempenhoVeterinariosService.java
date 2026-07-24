package br.com.api.petpoints.domain.users.gerente.features.desempenhoveterinarios.service;

import br.com.api.petpoints.domain.users.gerente.features.desempenhoveterinarios.form.RelatorioDesempenhoVeterinariosForm;

public interface RelatorioDesempenhoVeterinariosService {
    byte[] gerarRelatorio(RelatorioDesempenhoVeterinariosForm form);
}
