package br.com.api.petpoints.shared.features.payment.controller;

import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;

record CobrancaResponse(String id, String qrCodeCopiaECola, StatusPagamentoEnum status) {
}
