package br.com.api.petpoints.shared.models;

import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comprovante")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComprovanteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "enviado_por")
    private UsuarioModel enviadoPor;

    @CreationTimestamp
    @Column(name = "enviado_em")
    private LocalDateTime enviadoEm;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento")
    private StatusPagamentoEnum statusPagamento;

    private UUID arquivo;
}
