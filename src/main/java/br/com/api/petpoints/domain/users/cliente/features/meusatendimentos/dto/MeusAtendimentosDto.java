package br.com.api.petpoints.domain.users.cliente.features.meusatendimentos.dto;

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
public class MeusAtendimentosDto {

    private Long id;
    private StatusAtendimentoEnum status;
    private String mensagem;
    private String dataSolicitacao;

    public MeusAtendimentosDto(AtendimentoModel atendimento) {
        this.id = atendimento.getId();
        this.status = atendimento.getStatus();
        this.mensagem = atendimento.getPrimeiraMensagem();
        this.dataSolicitacao = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(atendimento.getSolicitadoEm());
    }

    public static List<MeusAtendimentosDto> convert(List<AtendimentoModel> atendimentos) {
        return atendimentos.stream().map(MeusAtendimentosDto::new).toList();
    }
}
