package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto;

import br.com.api.petpoints.shared.models.ItemConsultaModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemCobrancaConsultaDto {

    private Long id;
    private String nome;
    private int quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;

    public ItemCobrancaConsultaDto(ItemConsultaModel item) {
        this.id = item.getId();
        this.nome = item.getNomeProduto();
        this.quantidade = item.getQuantidade();
        this.valorUnitario = item.getValorUnitario();
        this.valorTotal = item.valorTotal();
    }

    public static List<ItemCobrancaConsultaDto> convert(List<ItemConsultaModel> itens) {
        return itens.stream().map(ItemCobrancaConsultaDto::new).toList();
    }
}
