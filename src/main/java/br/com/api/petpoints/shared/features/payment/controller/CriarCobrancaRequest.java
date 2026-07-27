package br.com.api.petpoints.shared.features.payment.controller;

import java.math.BigDecimal;

record CriarCobrancaRequest(BigDecimal valor, String descricao, String idExterno) {
}
