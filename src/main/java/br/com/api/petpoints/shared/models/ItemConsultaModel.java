package br.com.api.petpoints.shared.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Item de cobrança lançado pelo veterinário ao finalizar uma consulta
 * (uma vacina aplicada, um medicamento utilizado, etc.).
 * <p>
 * O nome e o valor unitário são gravados como "fotografia" do produto no
 * momento da consulta — se o estoquista alterar o preço ou remover o produto
 * depois, a cobrança já emitida continua íntegra.
 */
@Entity
@Table(name = "item_consulta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemConsultaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consulta_id")
    private ConsultaModel consulta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id")
    private ProdutoModel produto;

    @Column(name = "nome_produto")
    private String nomeProduto;

    private int quantidade;

    @Column(name = "valor_unitario", precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    public ItemConsultaModel(ConsultaModel consulta, ProdutoModel produto, int quantidade) {
        this.consulta = consulta;
        this.produto = produto;
        this.nomeProduto = produto.getNome();
        this.quantidade = quantidade;
        this.valorUnitario = BigDecimal.valueOf(produto.getValorUnitario());
    }

    public BigDecimal valorTotal() {
        return this.valorUnitario.multiply(BigDecimal.valueOf(this.quantidade));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemConsultaModel that = (ItemConsultaModel) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
