package br.com.api.petpoints.shared.models;

import br.com.api.petpoints.domain.users.estoquista.features.movimentacoes.form.NovaMovimentacaoForm;
import br.com.api.petpoints.shared.enums.TipoMovimentacaoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacao")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovimentacaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoMovimentacaoEnum tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id")
    private ProdutoModel produto;

    @CreationTimestamp
    @Column(name = "movimentado_em")
    private LocalDateTime movimentadoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movimentado_por")
    private UsuarioModel movimentadoPor;

    @Column(name = "quantidade_movimentada")
    private int quantidadeMovimentada;

    public MovimentacaoModel(NovaMovimentacaoForm form, UsuarioModel usuario, ProdutoModel produto) {
        this.movimentadoPor = usuario;
        this.produto = produto;
        this.tipo = form.getTipoMovimentacao();
        this.quantidadeMovimentada = form.getQuantidadeMovimentada();
    }
}
