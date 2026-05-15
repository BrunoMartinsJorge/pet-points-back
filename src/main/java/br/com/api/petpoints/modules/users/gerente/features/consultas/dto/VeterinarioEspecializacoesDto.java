package br.com.api.petpoints.modules.users.gerente.features.consultas.dto;

import br.com.api.petpoints.shared.models.EspecializacaoModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VeterinarioEspecializacoesDto {

    private Long id;
    private String nome;
    private List<EspecializacoesDto> especializacoes;

    public VeterinarioEspecializacoesDto(UsuarioModel usuario, List<EspecializacaoModel> especializacoes) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.especializacoes = EspecializacoesDto.convert(especializacoes);
    }

    public static List<VeterinarioEspecializacoesDto> convert(List<EspecializacaoModel> especializacoes) {

        Map<UsuarioModel, List<EspecializacaoModel>> agrupado = new HashMap<>();
        for (EspecializacaoModel esp : especializacoes) {
            for (UsuarioModel vet : esp.getVeterinarios()) {
                agrupado
                        .computeIfAbsent(vet, k -> new ArrayList<>())
                        .add(esp);
            }
        }
        return agrupado.entrySet().stream()
                .map(entry -> new VeterinarioEspecializacoesDto(
                        entry.getKey(),
                        entry.getValue()
                ))
                .toList();
    }
}
