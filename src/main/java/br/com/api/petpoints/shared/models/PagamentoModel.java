package br.com.api.petpoints.shared.models;

import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pagamento")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagamentoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor_pagamento")
    private double valorPagamento;

    @CreationTimestamp
    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Column(name = "data_limite_pagamento")
    private LocalDateTime dataLimitePagamento;

    @ManyToOne
    @JoinColumn(name = "aprovado_por")
    private UsuarioModel aprovadoPor;

    @ManyToOne
    @JoinColumn(name = "emitido_por")
    private UsuarioModel emitidoPor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento")
    private StatusPagamentoEnum statusPagamento = StatusPagamentoEnum.PENDENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pagamento")
    private TipoPagamentoEnum tipoPagamento;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "comprovante_id")
    private ComprovanteModel comprovante;

    @Column(name = "motivo_indeferimento")
    private String motivoIndeferimento;
}
