package br.com.api.petpoints.domain.users.atendente.features.pagamentos.controller;

import br.com.api.petpoints.domain.users.atendente.features.pagamentos.dto.CardsPagamentosClinica;
import br.com.api.petpoints.domain.users.atendente.features.pagamentos.dto.DetalhesPagamentoClinicaDto;
import br.com.api.petpoints.domain.users.atendente.features.pagamentos.dto.PagamentosClinicaDto;
import br.com.api.petpoints.domain.users.atendente.features.pagamentos.forms.IndeferirPagamentoClinicaForm;
import br.com.api.petpoints.domain.users.atendente.features.pagamentos.service.PagamentosClinicaAtendenteServiceImpl;
import br.com.api.petpoints.shared.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/atendente/pagamentos")
public class PagamentosClinicaAtendenteController {

    private final PagamentosClinicaAtendenteServiceImpl pagamentosClinicaAtendenteService;

    @GetMapping("/cards")
    public ResponseEntity<CardsPagamentosClinica> buscarCardsPagamentos(HttpServletRequest request) {
        return ResponseEntity.ok().body(this.pagamentosClinicaAtendenteService.buscarCardsPagamentoClinica(TokenUtils.getIdUsuario(request)));
    }

    @GetMapping("/historico")
    public ResponseEntity<List<PagamentosClinicaDto>> buscarHistoricoPagamentos() {
        return ResponseEntity.ok().body(this.pagamentosClinicaAtendenteService.buscarHistoricoPagamentosClinica());
    }

    @GetMapping("/pendentes-atrasados")
    public ResponseEntity<List<PagamentosClinicaDto>> buscarPagamentosPendentesAtrasados() {
        return ResponseEntity.ok().body(this.pagamentosClinicaAtendenteService.buscarPagamentosPendentesAtrasados());
    }

    @PutMapping("/registrar-pagamento/{idPagamento}")
    public ResponseEntity<Void> registrarPagamento(HttpServletRequest request, @PathVariable Long idPagamento) {
        this.pagamentosClinicaAtendenteService.registrarPagamento(TokenUtils.getIdUsuario(request), idPagamento);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{idPagamento}")
    public ResponseEntity<DetalhesPagamentoClinicaDto> buscarDetalhesPagamento(@PathVariable Long idPagamento) {
        return ResponseEntity.ok().body(this.pagamentosClinicaAtendenteService.buscarDetalhesPagamento(idPagamento));
    }

    @PutMapping("/consultar-status/{idPagamento}")
    public ResponseEntity<DetalhesPagamentoClinicaDto> consultarStatusTransacao(@PathVariable Long idPagamento) {
        return ResponseEntity.ok().body(this.pagamentosClinicaAtendenteService.consultarStatusTransacao(idPagamento));
    }

    @PutMapping("/indeferir/{idPagamento}")
    public ResponseEntity<Void> indeferirPagamento(HttpServletRequest request, @PathVariable Long idPagamento, @RequestBody IndeferirPagamentoClinicaForm form) {
        this.pagamentosClinicaAtendenteService.indeferirPagamento(TokenUtils.getIdUsuario(request), idPagamento, form);
        return ResponseEntity.ok().build();
    }
}
