package br.com.api.petpoints.modules.users.cliente.features.meuspagamentos.dto;

import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetalhesPagamentoConsultaDto {

    private Long idPagamento;
    private Long idConsulta;
    private String veterinario;
    private String pet;
    private StatusConsultaEnum statusConsulta;
    private StatusPagamentoEnum statusPagamento;
    private String tipoConsulta;
}
