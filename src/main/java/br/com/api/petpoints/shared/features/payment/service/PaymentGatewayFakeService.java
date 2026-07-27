package br.com.api.petpoints.shared.features.payment.service;

import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import br.com.api.petpoints.shared.features.payment.model.CobrancaPix;
import br.com.api.petpoints.shared.features.payment.repository.CobrancaPixRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentGatewayFakeService {

    private final CobrancaPixRepository cobrancaPixRepository;

    public CobrancaPix criarCobrancaPix(BigDecimal valor, String descricao, String idExterno) {
        CobrancaPix c = new CobrancaPix();
        c.setIdExterno(idExterno);
        c.setValor(valor);
        c.setDescricao(descricao);
        c.setStatus(StatusPagamentoEnum.PENDENTE);
        c.setQrCodeCopiaECola("00020126FAKE-" + UUID.randomUUID().toString().replace("-", ""));
        return cobrancaPixRepository.save(c);
    }

    public StatusPagamentoEnum consultarStatus(String idCobranca) {
        return cobrancaPixRepository.findById(idCobranca)
                .map(CobrancaPix::getStatus)
                .orElseThrow(() -> new RuntimeException("Cobrança não encontrada: " + idCobranca));
    }
}
