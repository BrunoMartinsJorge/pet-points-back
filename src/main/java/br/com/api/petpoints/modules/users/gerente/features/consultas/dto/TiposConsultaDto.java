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
public class TiposConsultaDto {

    private Long id;
    private String nome;
    private String descricao;
    private double valor;

    public TiposConsultaDto(TipoConsultaModel tipoConsulta) {
        this.id = tipoConsulta.getId();
        this.nome = tipoConsulta.getNome();
        this.descricao = tipoConsulta.getDescricao();
        this.valor = tipoConsulta.getValor();
    }

    public static List<TiposConsultaDto> convert(List<TipoConsultaModel> tiposConsulta) {
        return tiposConsulta.stream().map(TiposConsultaDto::new).toList();
    }
}
