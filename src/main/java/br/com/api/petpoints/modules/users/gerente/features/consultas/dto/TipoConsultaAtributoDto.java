package br.com.api.petpoints.modules.users.gerente.features.consultas.dto;

import br.com.api.petpoints.shared.models.TipoConsultaModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TipoConsultaAtributoDto {

    private Long id;
    private String nome;

    public TipoConsultaAtributoDto(TipoConsultaModel tipoConsulta) {
        this.id = tipoConsulta.getId();
        this.nome = tipoConsulta.getNome();
    }
}
