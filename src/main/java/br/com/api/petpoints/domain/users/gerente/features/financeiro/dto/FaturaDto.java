package br.com.api.petpoints.domain.users.gerente.features.financeiro.dto;

import br.com.api.petpoints.shared.models.PagamentoModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FaturaDto {

    private Long id;
    private String numero;
    private Long clienteId;
    private String clienteNome;
    private double valor;
    private String status;
    private LocalDateTime data;
    private String tipoPagamento;

    public FaturaDto(PagamentoModel pagamento) {
        this.id = pagamento.getId();
        this.numero = montarNumero(pagamento);
        this.clienteId = pagamento.getEmitidoPor() != null ? pagamento.getEmitidoPor().getId() : null;
        this.clienteNome = pagamento.getEmitidoPor() != null ? pagamento.getEmitidoPor().getNome() : "-";
        this.valor = pagamento.getValorPagamento();
        this.status = calcularStatus(pagamento);
        this.data = pagamento.getDataPagamento();
        this.tipoPagamento = pagamento.getTipoPagamento() != null ? pagamento.getTipoPagamento().getDescricao() : "-";
    }

    private static String montarNumero(PagamentoModel pagamento) {
        int ano = pagamento.getDataPagamento() != null ? pagamento.getDataPagamento().getYear() : LocalDate.now().getYear();
        return String.format("FAT-%d-%04d", ano, pagamento.getId());
    }

    private static String calcularStatus(PagamentoModel pagamento) {
        return switch (pagamento.getStatusPagamento()) {
            case APROVADO -> "PAGO";
            case REPROVADO -> "RECUSADO";
            case PENDENTE, ENVIADO -> {
                boolean vencido = pagamento.getDataLimitePagamento() != null
                        && pagamento.getDataLimitePagamento().toLocalDate().isBefore(LocalDate.now());
                yield vencido ? "ATRASADO" : "PENDENTE";
            }
        };
    }
}
