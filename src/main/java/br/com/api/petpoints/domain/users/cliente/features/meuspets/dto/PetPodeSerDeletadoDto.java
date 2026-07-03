package br.com.api.petpoints.domain.users.cliente.features.meuspets.dto;

import br.com.api.petpoints.shared.models.ConsultaModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PetPodeSerDeletadoDto {
    private boolean possuiConsultasEmAndamento;
    private List<ConsultasPendentesIniciadasDto> consultas;

    public PetPodeSerDeletadoDto(boolean possuiConsultasEmAndamento, List<ConsultaModel> consultas) {
        this.possuiConsultasEmAndamento = possuiConsultasEmAndamento;
        this.consultas = ConsultasPendentesIniciadasDto.convert(consultas);
    }
}
