package br.com.api.petpoints.domain.users.cliente.features.dashboard.dto;

import br.com.api.petpoints.shared.enums.StatusAtendimentoEnum;
import br.com.api.petpoints.shared.models.AtendimentoModel;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AtendimentosPendentesDto {

    private Long id;
    private String dataSolicitacao;
    private StatusAtendimentoEnum status;
    private String mensagem;

    public AtendimentosPendentesDto(AtendimentoModel atendimento) {
        this.id = atendimento.getId();
        this.dataSolicitacao = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(atendimento.getSolicitadoEm());
        this.status = atendimento.getStatus();
        this.mensagem = atendimento.getPrimeiraMensagem();
    }

    public static List<AtendimentosPendentesDto> convert(List<AtendimentoModel> atendimentos) {
        return atendimentos.stream().map(AtendimentosPendentesDto::new).toList();
    }
}
