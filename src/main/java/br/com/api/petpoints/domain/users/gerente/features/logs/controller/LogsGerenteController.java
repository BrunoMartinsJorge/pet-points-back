package br.com.api.petpoints.domain.users.gerente.features.logs.controller;

import br.com.api.petpoints.domain.users.gerente.features.logs.dto.LogDto;
import br.com.api.petpoints.domain.users.gerente.features.logs.dto.UsuariosLogsDto;
import br.com.api.petpoints.domain.users.gerente.features.logs.forms.RelatorioLogsForm;
import br.com.api.petpoints.domain.users.gerente.features.logs.service.LogsGerenteServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gerente/logs")
public class LogsGerenteController {

    private final LogsGerenteServiceImpl logsGerenteService;

    @GetMapping
    public ResponseEntity<List<LogDto>> listarTodosOsLogs() {
        return ResponseEntity.ok().body(this.logsGerenteService.listarLogs());
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuariosLogsDto>> listarTodosOsUsuarios() {
        return ResponseEntity.ok().body(this.logsGerenteService.listarUsuariosLogs());
    }

    @PutMapping("/relatorio")
    public ResponseEntity<byte[]> relatoriosPdf(@RequestBody RelatorioLogsForm form) {
        byte[] pdf = this.logsGerenteService.gerarRelatorio(form);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=RelatorioLogs.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
