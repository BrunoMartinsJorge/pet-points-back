package br.com.api.petpoints.modules.gerente.features.logs.forms;

import br.com.api.petpoints.shared.enums.TipoLogEnum;
import jakarta.annotation.Nullable;
import lombok.Getter;

@Getter
public class RelatorioLogsForm {

    Long idUsuario;
    String datas;
    TipoLogEnum tipo;
}
