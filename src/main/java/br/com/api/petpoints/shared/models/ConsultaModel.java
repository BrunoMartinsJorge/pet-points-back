package br.com.api.petpoints.shared.models;

import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

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
    private StatusConsultaEnum status;

    @ManyToOne
    @JoinColumn(name = "solicitante_id")
    private UsuarioModel solicitante;

    @ManyToOne
    @JoinColumn(name = "atendente_id")
    private UsuarioModel atendente;

    @ManyToOne
    @JoinColumn(name = "veterinario_id")
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "avaliacao_id")
    private AvaliacaoModel avaliacao;
}
