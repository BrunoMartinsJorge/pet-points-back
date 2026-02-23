package br.com.api.petpoints.shared.features.logs;

import br.com.api.petpoints.modules.usuario.models.UsuarioModel;
import br.com.api.petpoints.shared.enums.TipoLogEnum;

public interface LogsService {
    public void registrarLog(UsuarioModel usuario, TipoLogEnum tipoLog);
}
