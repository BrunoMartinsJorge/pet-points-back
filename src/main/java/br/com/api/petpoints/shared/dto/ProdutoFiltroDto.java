package br.com.api.petpoints.shared.dto;

import br.com.api.petpoints.shared.models.ProdutoModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoFiltroDto {

    private Long id;
    private String nome;

    public ProdutoFiltroDto(ProdutoModel produto) {
        this.id = produto.getId();
        this.nome = produto.getNome();
    }

    public static List<ProdutoFiltroDto> convert(List<ProdutoModel> produtos) {
        return produtos.stream().map(ProdutoFiltroDto::new).toList();
    }
}
