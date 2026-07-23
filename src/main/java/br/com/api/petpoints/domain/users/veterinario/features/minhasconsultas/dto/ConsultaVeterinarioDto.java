package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto;

import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.models.*;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConsultaVeterinarioDto {

    private Long id;
    private StatusConsultaEnum status;
    private String pet;
    private String cliente;
    private LocalDateTime data;
    private UUID imagemCliente;
    private String tipo;
    private String observacoes;

    public ConsultaVeterinarioDto(ConsultaModel consulta) {
        id = consulta.getId();
        status = consulta.getStatus();
        pet = consulta.getPet().getNome();
        cliente = consulta.getSolicitante().getNome();
        tipo = consulta.getTipoConsulta().getNome();
        data = consulta.getDataConsulta();
        observacoes = consulta.getObservacoes();
        imagemCliente = consulta.getSolicitante().getImagem() != null ? consulta.getSolicitante().getImagem() : null;
    }

    public static List<ConsultaVeterinarioDto> convert(List<ConsultaModel> consultas) {
        return consultas.stream().map(ConsultaVeterinarioDto::new).toList();
    }
}
