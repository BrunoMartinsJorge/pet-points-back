package br.com.api.petpoints.domain.users.gerente.features.logs.forms;

import br.com.api.petpoints.shared.enums.TipoLogEnum;
import lombok.Getter;

@Getter
public class RelatorioLogsForm {

    Long idUsuario;
    String datas;
    TipoLogEnum tipo;
}
