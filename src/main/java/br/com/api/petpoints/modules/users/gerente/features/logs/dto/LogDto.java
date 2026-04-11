package br.com.api.petpoints.modules.users.gerente.features.logs.dto;

import br.com.api.petpoints.shared.enums.TipoLogEnum;
import br.com.api.petpoints.shared.models.LogsModel;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LogDto {

    private String mensagem;
    private TipoLogEnum tipo;
    private String lancadoPor;
    private Long lancadoPorId;
    private String registradoEm;

    public LogDto(LogsModel log) {
        this.mensagem = log.getMensagem();
        this.tipo = log.getTipo();
        this.lancadoPor = log.getLancadoPor() != null ? log.getLancadoPor().getNome() : null;
        this.lancadoPorId = log.getLancadoPor() != null ? log.getLancadoPor().getId() : null;
        this.registradoEm = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(log.getRegistradoEm());
    }
}
