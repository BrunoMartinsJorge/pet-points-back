package br.com.api.petpoints.domain.users.gerente.features.logs.service;

import br.com.api.petpoints.domain.users.gerente.features.logs.dto.LogDto;
import br.com.api.petpoints.domain.users.gerente.features.logs.dto.UsuariosLogsDto;
import br.com.api.petpoints.domain.users.gerente.features.logs.forms.RelatorioLogsForm;

import java.util.List;

public interface LogsGerenteService {

    List<LogDto> listarLogs();
    List<UsuariosLogsDto> listarUsuariosLogs();
    byte[] gerarRelatorio(RelatorioLogsForm form);
}
