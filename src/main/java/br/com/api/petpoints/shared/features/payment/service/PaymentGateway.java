package br.com.api.petpoints.shared.features.payment.service;

import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import br.com.api.petpoints.shared.features.payment.model.CobrancaPix;

import java.math.BigDecimal;

public interface PaymentGateway {
    CobrancaPix criarCobrancaPix(BigDecimal valor, String descricao, String idExterno);
    StatusPagamentoEnum consultarStatus(String idCobranca);
}
