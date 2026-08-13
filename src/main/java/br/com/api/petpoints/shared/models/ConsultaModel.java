package br.com.api.petpoints.shared.models;

import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consulta")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsultaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "iniciado_em")
    private LocalDateTime iniciadoEm;

    @Column(name = "finalizado_em")
    private LocalDateTime finalizadoEm;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_consulta")
    private StatusConsultaEnum status = StatusConsultaEnum.PENDENTE;

    @ManyToOne
    @JoinColumn(name = "solicitante_id")
    private UsuarioModel solicitante;

    @ManyToOne
    @JoinColumn(name = "atendente_id")
    private UsuarioModel atendente;

    @ManyToOne
    private UsuarioModel veterinario;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private PetModel pet;

    @JoinColumn(name = "tipo_consulta_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoConsultaModel tipoConsulta;

    @CreationTimestamp
    @Column(name = "solicitado_em")
    private LocalDateTime solicitadoEm;

    @Column(name = "deferido_em")
    private LocalDateTime deferidoEm;

    @Length(min = 8, max = 200)
    @Column(name = "motivo_indeferimento")
    private String motivoIndeferimento;

    @Column(name = "data_consulta")
    private LocalDateTime dataConsulta;

    @Column(name = "resumo_consulta")
    private String resumoConsulta;

    @Column(name = "cancelado_em")
    private LocalDateTime canceladoEm;

    @Column(name = "motivo_cancelamento")
    private String motivoCancelamento;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pagamento_id")
    private PagamentoModel pagamento;

    /**
     * Forma de pagamento escolhida pelo cliente (PIX, cartão ou dinheiro).
     * É apenas a "preferência" enquanto a consulta ainda não foi finalizada
     * (ainda não existe cobrança). Quando o veterinário finaliza a consulta,
     * este valor é usado para decidir se será gerada uma cobrança PIX (Mercado Pago)
     * ou uma cobrança presencial (aguardando validação do atendente).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento_selecionada")
    private TipoPagamentoEnum formaPagamento;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "avaliacao_id")
    private AvaliacaoModel avaliacao;

    private String observacoes;

    /**
     * Itens (vacinas, medicamentos, etc.) lançados pelo veterinário no momento
     * da finalização da consulta. Somados ao valor do tipo de consulta, formam
     * o valor total da cobrança gerada para o cliente.
     */
    @OneToMany(mappedBy = "consulta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemConsultaModel> itensCobranca = new ArrayList<>();

    public BigDecimal valorConsulta() {
        return this.tipoConsulta != null
                ? BigDecimal.valueOf(this.tipoConsulta.getValor())
                : BigDecimal.ZERO;
    }

    public BigDecimal valorItensCobranca() {
        return this.itensCobranca.stream()
                .map(ItemConsultaModel::valorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal valorTotalCobranca() {
        return this.valorConsulta().add(this.valorItensCobranca());
    }
}
