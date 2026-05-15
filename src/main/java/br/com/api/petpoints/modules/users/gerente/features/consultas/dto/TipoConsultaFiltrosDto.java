package br.com.api.petpoints.modules.users.gerente.features.consultas.dto;

import br.com.api.petpoints.shared.models.TipoConsultaModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TipoConsultaFiltrosDto {

    private Long value;
    private String label;

    public TipoConsultaFiltrosDto(TipoConsultaModel tipoConsulta) {
        this.value = tipoConsulta.getId();
        this.label = tipoConsulta.getNome();
    }

    public static List<TipoConsultaFiltrosDto> convert(List<TipoConsultaModel> tipoConsultas) {
        return tipoConsultas.stream().map(TipoConsultaFiltrosDto::new).toList();
    }
}
