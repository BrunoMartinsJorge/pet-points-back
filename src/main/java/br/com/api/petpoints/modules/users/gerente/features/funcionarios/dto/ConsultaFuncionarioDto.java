package br.com.api.petpoints.modules.users.gerente.features.funcionarios.dto;

import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.models.ConsultaModel;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConsultaFuncionarioDto {

    private Long id;
    private String tipo;
    private StatusConsultaEnum status;
    private String observacoes;
    private String dataConsulta;

    public ConsultaFuncionarioDto(ConsultaModel consulta) {
        this.id = consulta.getId();
        this.tipo = consulta.getTipoConsulta().getNome();
        this.status = consulta.getStatus();
        this.observacoes = consulta.getObservacoes();
        this.dataConsulta = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(consulta.getDataConsulta());
    }

    public static List<ConsultaFuncionarioDto> convert(List<ConsultaModel> consultas) {
        return consultas.stream().map(ConsultaFuncionarioDto::new).toList();
    }
}
