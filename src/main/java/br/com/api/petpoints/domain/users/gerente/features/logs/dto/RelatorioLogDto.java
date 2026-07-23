package br.com.api.petpoints.domain.users.gerente.features.logs.dto;

import br.com.api.petpoints.shared.models.LogsModel;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RelatorioLogDto {

    private Long id;
    private String tipo;
    private String email;
    private String dataHora;
    private String mensagem;

    public RelatorioLogDto(LogsModel log) {
        this.id = log.getId();
        this.tipo = log.getTipo().toString();
        this.email = log.getLancadoPor() != null ? log.getLancadoPor().getEmail() : "Usuário Não Encontrado!";
        this.dataHora = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(log.getRegistradoEm());
        this.mensagem = log.getMensagem();
    }

    public static List<RelatorioLogDto> converter(List<LogsModel> logs) {
        return logs.stream().map(RelatorioLogDto::new).toList();
    }
}
