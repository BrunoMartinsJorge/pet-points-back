package br.com.api.petpoints.domain.users.gerente.features.desempenhoveterinarios.controller;

import br.com.api.petpoints.domain.users.gerente.features.desempenhoveterinarios.form.RelatorioDesempenhoVeterinariosForm;
import br.com.api.petpoints.domain.users.gerente.features.desempenhoveterinarios.service.RelatorioDesempenhoVeterinariosServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gerente/desempenho-veterinarios")
@RequiredArgsConstructor
public class RelatorioDesempenhoVeterinariosController {

    private final RelatorioDesempenhoVeterinariosServiceImpl relatorioDesempenhoVeterinariosService;

    @PostMapping("/relatorio")
    public ResponseEntity<byte[]> gerarRelatorioDesempenhoVeterinarios(@RequestBody RelatorioDesempenhoVeterinariosForm form) {
        byte[] pdf = this.relatorioDesempenhoVeterinariosService.gerarRelatorio(form);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=RelatorioDesempenhoVeterinarios.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
