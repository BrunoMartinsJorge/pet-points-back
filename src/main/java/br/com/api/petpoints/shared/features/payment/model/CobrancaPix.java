package br.com.api.petpoints.shared.features.payment.model;

import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_cobrancao_pix")
public class CobrancaPix {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String idExterno;
    private BigDecimal valor;
    private String descricao;
    private String qrCodeCopiaECola;
    @Enumerated(EnumType.STRING)
    private StatusPagamentoEnum status = StatusPagamentoEnum.PENDENTE;
    @CreationTimestamp
    private LocalDateTime criadoEm;
    private String gatewayId;
    private LocalDateTime pagoEm;
}
