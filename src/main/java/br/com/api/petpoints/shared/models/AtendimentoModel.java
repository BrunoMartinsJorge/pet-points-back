package br.com.api.petpoints.shared.models;

import br.com.api.petpoints.shared.enums.StatusAtendimentoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "atendimento")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtendimentoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private ChatModel chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private UsuarioModel cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atendente_id")
    private UsuarioModel atendente;

    @Column(name = "solicitado_em")
    @CreationTimestamp
    private LocalDateTime solicitadoEm;

    @Column(name = "iniciado_em")
    private LocalDateTime iniciadoEm;

    @Column(name = "finalizado_em")
    private LocalDateTime finalizadoEm;

    @Column(name = "status_atendimento")
    private StatusAtendimentoEnum status;

    @Column(name = "primeira_mensagem")
    private String primeiraMensagem;

    @OneToOne
    @JoinColumn(name = "avaliacao_id")
    private AvaliacaoModel avaliacao;
}
