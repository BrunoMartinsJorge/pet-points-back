package br.com.api.petpoints.modules.users.gerente.features.consultas.dto;

import br.com.api.petpoints.shared.models.EspecializacaoModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VeterinarioTipoConsultaDto {

    private Long id;
    private String nome;
    private List<String> especializacao;

    public VeterinarioTipoConsultaDto(UsuarioModel usuario, List<EspecializacaoModel> especializacao) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.especializacao = especializacao.stream().map(EspecializacaoModel::getDescricao).toList();
    }

    public static List<VeterinarioTipoConsultaDto> converter(List<EspecializacaoModel> especializacoes) {
        List<VeterinarioTipoConsultaDto> veterinarioTipoConsultaDtos = new ArrayList<>();
        for (EspecializacaoModel especializacao : especializacoes) {
            for (UsuarioModel veterinario : especializacao.getVeterinarios()) {
                veterinarioTipoConsultaDtos.add(new VeterinarioTipoConsultaDto(veterinario, List.of(especializacao)));
            }
        }
        return veterinarioTipoConsultaDtos;
    }
}
