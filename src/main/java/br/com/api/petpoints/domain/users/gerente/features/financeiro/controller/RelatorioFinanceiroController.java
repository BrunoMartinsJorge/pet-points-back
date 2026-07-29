package br.com.api.petpoints.domain.users.gerente.features.financeiro.controller;

import br.com.api.petpoints.domain.users.gerente.features.financeiro.form.RelatorioFinanceiroForm;
import br.com.api.petpoints.domain.users.gerente.features.financeiro.service.RelatorioFinanceiroServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gerente/financeiro")
@RequiredArgsConstructor
public class RelatorioFinanceiroController {

    private final RelatorioFinanceiroServiceImpl relatorioFinanceiroService;

    @PostMapping("/relatorio")
    public ResponseEntity<byte[]> gerarRelatorioFinanceiro(@RequestBody RelatorioFinanceiroForm form) {
        byte[] pdf = this.relatorioFinanceiroService.gerarRelatorio(form);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=RelatorioFinanceiro.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
