package br.com.api.petpoints.shared.features.payment.service;

import br.com.api.petpoints.core.api.MercadoPagoProperties;
import br.com.api.petpoints.shared.features.payment.dto.MercadoPagoDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Camada de comunicação com a API do Mercado Pago.
 * Não conhece o seu banco — só faz as chamadas HTTP e devolve os DTOs do MP.
 */
@Service
public class MercadoPagoService {

    private final RestClient restClient;
    private final MercadoPagoProperties properties;

    public MercadoPagoService(MercadoPagoProperties properties) {
        this.properties = properties;

        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.accessToken())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /** POST /v1/orders — cria a order (o X-Idempotency-Key é único por chamada). */
    public MercadoPagoDto.OrderResponse criarOrder(MercadoPagoDto.OrderRequest request) {
        return restClient.post()
                .uri("/v1/orders")
                .header("X-Idempotency-Key", UUID.randomUUID().toString())
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    String corpo = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                    throw new MercadoPagoException(
                            "Erro ao criar order no Mercado Pago (" + res.getStatusCode() + "): " + corpo);
                })
                .body(MercadoPagoDto.OrderResponse.class);
    }

    /** GET /v1/orders/{id} — consulta a order (status atualizado do pagamento). */
    public MercadoPagoDto.OrderResponse buscarOrder(String orderId) {
        return restClient.get()
                .uri("/v1/orders/{id}", orderId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    String corpo = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                    throw new MercadoPagoException(
                            "Erro ao buscar order " + orderId + " (" + res.getStatusCode() + "): " + corpo);
                })
                .body(MercadoPagoDto.OrderResponse.class);
    }

    /** GET /v1/payment_methods/search — lista os métodos de pagamento disponíveis. */
    public List<MercadoPagoDto.PaymentMethodInfo> buscarMetodosPagamento(String marketplace) {
        MercadoPagoDto.PaymentMethodInfo[] metodos = restClient.get()
                .uri(uri -> uri.path("/v1/payment_methods/search")
                        .queryParam("public_key", properties.publicKey())
                        .queryParam("marketplace", marketplace)
                        .build())
                .retrieve()
                .body(MercadoPagoDto.PaymentMethodInfo[].class);

        return metodos == null ? List.of() : Arrays.asList(metodos);
    }

    /** Exceção simples para erros vindos da API do MP. */
    public static class MercadoPagoException extends RuntimeException {
        public MercadoPagoException(String message) {
            super(message);
        }
    }
}
