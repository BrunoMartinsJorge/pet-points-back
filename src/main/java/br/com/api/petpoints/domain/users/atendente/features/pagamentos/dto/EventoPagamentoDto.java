package br.com.api.petpoints.domain.users.atendente.features.pagamentos.dto;

import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventoPagamentoDto {

    private String titulo;
    private String descricao;
    private String responsavel;
    private LocalDateTime data;
    private StatusPagamentoEnum status;
}
