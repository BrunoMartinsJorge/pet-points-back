package br.com.api.petpoints.domain.users.atendente.features.consultas.dto;

import br.com.api.petpoints.shared.models.UsuarioModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Opção de cliente usada no registro de consultas feito pelo atendente.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OpcaoClienteConsultaDto {

    private Long value;
    private String label;

    public OpcaoClienteConsultaDto(UsuarioModel cliente) {
        this.value = cliente.getId();
        this.label = cliente.getNome();
    }

    public static List<OpcaoClienteConsultaDto> convert(List<UsuarioModel> clientes) {
        return clientes.stream().map(OpcaoClienteConsultaDto::new).toList();
    }
}
