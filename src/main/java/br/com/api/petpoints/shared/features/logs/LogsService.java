package br.com.api.petpoints.shared.features.logs;

import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.enums.TipoLogEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

public interface LogsService {
    void registrarLog(UsuarioModel usuario, TipoLogEnum tipoLog);
    void registrarException(Exception ex, HttpServletRequest request, HttpStatus status);
}
