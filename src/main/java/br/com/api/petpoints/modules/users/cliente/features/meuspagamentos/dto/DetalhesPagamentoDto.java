package br.com.api.petpoints.modules.users.cliente.features.meuspagamentos.dto;

import br.com.api.petpoints.shared.models.ArquivosModel;
import br.com.api.petpoints.shared.models.PagamentoModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetalhesPagamentoDto {

    private String atendente;
    private String motivoIndeferimento;
    private String tituloArquivo;
    private String tipoArquivo;
    private UUID uuid;

    public DetalhesPagamentoDto(PagamentoModel pagamento, ArquivosModel comprovante) {
        this.atendente = pagamento.getAprovadoPor() != null ? pagamento.getAprovadoPor().getNome() : "Sem Atendente";
        this.motivoIndeferimento = pagamento.getMotivoIndeferimento();
        this.tituloArquivo = comprovante != null ? comprovante.getNome() : null;
        this.tipoArquivo = comprovante != null ? comprovante.getTipo() : null;
        this.uuid = comprovante != null ? comprovante.getId() : null;
    }
}
