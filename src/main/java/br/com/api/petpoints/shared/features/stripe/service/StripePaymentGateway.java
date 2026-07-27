package br.com.api.petpoints.shared.features.stripe.service;

import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import br.com.api.petpoints.shared.features.payment.model.CobrancaPix;
import br.com.api.petpoints.shared.features.payment.repository.CobrancaPixRepository;
import br.com.api.petpoints.shared.features.payment.service.PaymentGateway;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Profile("stripe")
public class StripePaymentGateway implements PaymentGateway {

    private final CobrancaPixRepository cobrancaPixRepository;


    @Override
    public CobrancaPix criarCobrancaPix(BigDecimal valor, String descricao, String idExterno) {
        try {
            long centavos = valor.movePointRight(2).longValueExact(); // Stripe usa centavos

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(centavos)
                    .setCurrency("brl")
                    .addPaymentMethodType("pix")
                    .setDescription(descricao)
                    .putMetadata("idExterno", idExterno)
                    .setConfirm(true)
                    .setPaymentMethodData(
                            PaymentIntentCreateParams.PaymentMethodData.builder()
                                    .setType(PaymentIntentCreateParams.PaymentMethodData.Type.PIX)
                                    .build())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            // O "copia e cola" e a imagem do QR vêm dentro do next_action:
            var qr = intent.getNextAction().getPixDisplayQrCode();

            CobrancaPix c = new CobrancaPix();
            c.setIdExterno(idExterno);
            c.setValor(valor);
            c.setDescricao(descricao);
            c.setGatewayId(intent.getId());       // pi_... (referência na Stripe)
            c.setQrCodeCopiaECola(qr.getData());  // string EMV do Pix (copia e cola)
            c.setStatus(StatusPagamentoEnum.PENDENTE);
            c.setCriadoEm(LocalDateTime.now());
            return cobrancaPixRepository.save(c);

        } catch (StripeException e) {
            throw new RuntimeException("Erro ao criar cobrança na Stripe: " + e.getMessage(), e);
        }
    }

    @Override
    public StatusPagamentoEnum consultarStatus(String idCobranca) {
        CobrancaPix c = cobrancaPixRepository.findById(idCobranca)
                .orElseThrow(() -> new RuntimeException("Cobrança não encontrada: " + idCobranca));
        try {
            PaymentIntent intent = PaymentIntent.retrieve(c.getGatewayId());
            StatusPagamentoEnum status = mapear(intent.getStatus());
            if (status != c.getStatus()) {
                c.setStatus(status);
                if (status == StatusPagamentoEnum.APROVADO) c.setPagoEm(LocalDateTime.now());
                cobrancaPixRepository.save(c);
            }
            return status;
        } catch (StripeException e) {
            throw new RuntimeException("Erro ao consultar status na Stripe: " + e.getMessage(), e);
        }
    }

    private StatusPagamentoEnum mapear(String stripeStatus) {
        return switch (stripeStatus) {
            case "succeeded" -> StatusPagamentoEnum.APROVADO;
            case "canceled" -> StatusPagamentoEnum.REPROVADO;
            default -> StatusPagamentoEnum.PENDENTE;
        };
    }
}
