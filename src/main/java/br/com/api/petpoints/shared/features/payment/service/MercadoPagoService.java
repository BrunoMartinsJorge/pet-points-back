package br.com.api.petpoints.shared.features.payment.service;

import br.com.api.petpoints.core.api.MercadoPagoProperties;
import br.com.api.petpoints.shared.features.payment.dto.MercadoPagoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
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

        log.info("Cliente Mercado Pago inicializado. Base URL: {}", properties.baseUrl());
    }

    /**
     * Cria uma nova ordem de pagamento no Mercado Pago utilizando o método PIX.
     * <p>
     * A requisição é enviada para a API do Mercado Pago contendo todas as
     * informações necessárias para geração da cobrança. Cada requisição recebe
     * uma chave de idempotência única, evitando a criação de pagamentos
     * duplicados em caso de reenvio.
     *
     * @param request Dados necessários para criação da ordem de pagamento.
     * @return Resposta da API contendo os dados da ordem criada.
     * @throws MercadoPagoException Caso a API do Mercado Pago retorne erro.
     */
    public MercadoPagoDto.OrderResponse criarOrder(MercadoPagoDto.OrderRequest request) {

        String idempotencyKey = UUID.randomUUID().toString();

        log.info(
                "Iniciando criação de ordem de pagamento. Cliente: {}, Valor: {}, Idempotency-Key: {}",
                request.payer().email(),
                request.transactions().payments().getFirst().amount(),
                idempotencyKey);

        try {

            MercadoPagoDto.OrderResponse response = restClient.post()
                    .uri("/v1/orders")
                    .header("X-Idempotency-Key", idempotencyKey)
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        String corpo = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);

                        log.error(
                                "Erro ao criar ordem no Mercado Pago. Status: {}, Resposta: {}",
                                res.getStatusCode(),
                                corpo);

                        throw new MercadoPagoException(
                                "Erro ao criar ordem no Mercado Pago ("
                                        + res.getStatusCode()
                                        + "): "
                                        + corpo);
                    })
                    .body(MercadoPagoDto.OrderResponse.class);

            log.info(
                    "Ordem criada com sucesso. OrderId: {}, Status: {}",
                    response != null ? response.id() : "SEM ID",
                    response != null ? response.status() : "SEM STATUS");

            return response;

        } catch (MercadoPagoException e) {
            throw e;
        } catch (Exception e) {

            log.error(
                    "Falha inesperada ao criar ordem de pagamento para o documento {}.",
                    request.payer().identification().number(),
                    e);

            throw new RuntimeException("Erro ao comunicar com o Mercado Pago.", e);
        }
    }

    /**
     * Consulta uma ordem de pagamento existente através do seu identificador.
     *
     * @param orderId Identificador da ordem de pagamento.
     * @return Dados atualizados da ordem.
     * @throws MercadoPagoException Caso a API retorne erro durante a consulta.
     */
    public MercadoPagoDto.OrderResponse buscarOrder(String orderId) {

        log.info("Consultando ordem de pagamento {}.", orderId);

        MercadoPagoDto.OrderResponse response = restClient.get()
                .uri("/v1/orders/{id}", orderId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    String corpo = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);

                    log.error(
                            "Erro ao consultar ordem {}. Status: {}, Resposta: {}",
                            orderId,
                            res.getStatusCode(),
                            corpo);

                    throw new MercadoPagoException(
                            "Erro ao buscar order "
                                    + orderId
                                    + " ("
                                    + res.getStatusCode()
                                    + "): "
                                    + corpo);
                })
                .body(MercadoPagoDto.OrderResponse.class);

        log.info(
                "Consulta realizada com sucesso. OrderId: {}, Status: {}",
                response != null ? response.id() : "SEM ID",
                response != null ? response.status() : "SEM STATUS");

        return response;
    }

    /**
     * Consulta os métodos de pagamento disponíveis para utilização.
     * <p>
     * Quando o parâmetro {@code marketplace} for nulo, a API considera o
     * comportamento padrão (PIX).
     *
     * @param marketplace Marketplace utilizado na consulta (opcional).
     * @return Lista de métodos de pagamento disponíveis. Nunca retorna
     *         {@code null}.
     */
    public List<MercadoPagoDto.PaymentMethodInfo> buscarMetodosPagamento(String marketplace) {

        log.info("Consultando métodos de pagamento. Marketplace: {}", marketplace);

        MercadoPagoDto.PaymentMethodInfo[] metodos = restClient.get()
                .uri(uri -> uri.path("/v1/payment_methods/search")
                        .queryParam("public_key", properties.publicKey())
                        .queryParam("marketplace", marketplace)
                        .build())
                .retrieve()
                .body(MercadoPagoDto.PaymentMethodInfo[].class);

        int quantidade = metodos == null ? 0 : metodos.length;

        log.info("{} método(s) de pagamento encontrado(s).", quantidade);

        return metodos == null ? List.of() : Arrays.asList(metodos);
    }

    /**
     * Exceção utilizada para representar erros retornados pela API do Mercado
     * Pago.
     */
    public static class MercadoPagoException extends RuntimeException {

        public MercadoPagoException(String message) {
            super(message);
        }
    }
}