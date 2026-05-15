package br.com.api.petpoints.modules.users.cliente.features.minhasconsultas.dto;

import br.com.api.petpoints.shared.models.EspecializacaoModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VeterinariosTipoConsultaDto {

    private Long id;
    private String nome;
    private double avaliacao;
    private String especializacoes;

    public VeterinariosTipoConsultaDto(UsuarioModel veterinario, List<EspecializacaoModel> especializacoes, double avaliacao) {
        this.id = veterinario.getId();
        this.nome = veterinario.getNome();
        this.avaliacao = avaliacao;
        this.especializacoes = this.getEspecializacoesString(especializacoes);
    }

    private String getEspecializacoesString(List<EspecializacaoModel> especializacoes) {
        StringBuilder especializacoesString = new StringBuilder();
        for (EspecializacaoModel especializacao : especializacoes) {
            especializacoesString.append('\n').append(especializacao.getDescricao()).append('\n');
        }
        return especializacoesString.toString();
    }
}
