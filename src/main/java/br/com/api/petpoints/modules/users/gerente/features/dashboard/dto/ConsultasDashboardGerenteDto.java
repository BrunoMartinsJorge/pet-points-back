package br.com.api.petpoints.modules.users.gerente.features.dashboard.dto;

import br.com.api.petpoints.shared.models.ConsultaModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConsultasDashboardGerenteDto {

    private String tipo;
    private String descricao;
    private LocalDateTime dataSolicitacao;

    public ConsultasDashboardGerenteDto(ConsultaModel consulta) {
        this.tipo = consulta.getTipoConsulta().getNome();
        this.descricao = consulta.getObservacoes();
        this.dataSolicitacao = consulta.getSolicitadoEm();
    }

    public static List<ConsultasDashboardGerenteDto> convert(List<ConsultaModel> consultas) {
        return consultas.stream().map(ConsultasDashboardGerenteDto::new).toList();
    }
}
