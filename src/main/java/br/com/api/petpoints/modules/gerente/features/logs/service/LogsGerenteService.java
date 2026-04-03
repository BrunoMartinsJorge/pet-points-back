package br.com.api.petpoints.modules.gerente.features.logs.service;

import br.com.api.petpoints.modules.gerente.features.logs.dto.LogDto;
import br.com.api.petpoints.modules.gerente.features.logs.dto.UsuariosLogsDto;
import br.com.api.petpoints.modules.gerente.features.logs.forms.RelatorioLogsForm;
import br.com.api.petpoints.shared.enums.TipoLogEnum;

import java.util.List;

public interface LogsGerenteService {

    List<LogDto> listarLogs();
    List<UsuariosLogsDto> listarUsuariosLogs();
    byte[] gerarRelatorio(RelatorioLogsForm form);
}
