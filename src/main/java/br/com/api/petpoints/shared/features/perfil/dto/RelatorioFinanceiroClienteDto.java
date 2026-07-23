package br.com.api.petpoints.shared.features.perfil.dto;

import br.com.api.petpoints.domain.users.cliente.features.meuspagamentos.dto.PagamentosDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioFinanceiroClienteDto {

    private int pagamentosPendentes;
    private double saldoPendente;
    private List<PagamentosDto> meusPagamentosAtrasadosPendentes;
}
