package br.com.api.petpoints.core.tasks;

import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.enums.TipoPagamentoEnum;
import br.com.api.petpoints.shared.features.payment.dto.PagamentoDto;
import br.com.api.petpoints.shared.features.payment.service.PagamentoService;
import br.com.api.petpoints.shared.models.ConsultaModel;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConsultasScheduling {

    private final ConsultaRepository consultaRepository;
    private final PagamentoService pagamentoService;

    @Scheduled(cron = "0 45 * * * *")
    public void taskPagamentosAtrasados() {
        List<ConsultaModel> consultas = consultaRepository.findAllByStatus(StatusConsultaEnum.FINALIZADO);
        if (!consultas.isEmpty()) {
            for (ConsultaModel consulta : consultas) {
                if (consulta.getPagamento() == null) return;
                if (!consulta.getFormaPagamento().equals(TipoPagamentoEnum.PIX)) return;
                PagamentoDto.StatusPagamentoResponse response = this.pagamentoService.consultarStatus(consulta.getPagamento().getId());
                log.info("Atualizando status com task de rotina da consulta {} - ORDER ID {} - Status do MP {}", consulta.getId(), response.orderId(), response.statusMercadoPago());
            }
        }
    }
}
