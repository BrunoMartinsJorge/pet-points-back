package br.com.api.petpoints.shared.features.payment.controller;

import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import br.com.api.petpoints.shared.features.payment.model.CobrancaPix;
import br.com.api.petpoints.shared.features.payment.repository.CobrancaPixRepository;
import br.com.api.petpoints.shared.features.payment.service.PaymentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final PaymentGateway gateway;
    private final CobrancaPixRepository repo;

    @PostMapping("/cobrancas")
    public CobrancaPix criar(@RequestBody CriarCobrancaRequest req) {
        return gateway.criarCobrancaPix(req.valor(), req.descricao(), req.idExterno());
    }

    @PostMapping("/webhook/simular/{id}")
    public ResponseEntity<String> simularConfirmacao(@PathVariable String id) {
        CobrancaPix c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cobrança não encontrada"));
        if (c.getStatus() == StatusPagamentoEnum.ENVIADO) {
            return ResponseEntity.ok("Cobrança já estava paga.");
        }
        c.setStatus(StatusPagamentoEnum.ENVIADO);
        c.setPagoEm(LocalDateTime.now());
        repo.save(c);
        // aqui você chama sua lógica de negócio: liberar consulta, notificar atendente, etc.
        return ResponseEntity.ok("Pagamento confirmado (simulado) para: " + id);
    }

    // 3) Reforço/reconciliação: a tela consulta o status
    @GetMapping("/cobrancas/{id}/status")
    public StatusPagamentoEnum status(@PathVariable String id) {
        return gateway.consultarStatus(id);
    }
}

