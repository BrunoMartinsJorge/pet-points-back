package br.com.api.petpoints.shared.features.arquivos.controller;

import br.com.api.petpoints.shared.features.arquivos.service.ArquivosServiceImpl;
import br.com.api.petpoints.shared.models.ArquivosModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/arquivos")
@RequiredArgsConstructor
public class ArquivosController {

    private final ArquivosServiceImpl arquivosService;

    @GetMapping("/{uuid}")
    public ResponseEntity<byte[]> buscarArquivo(@PathVariable("uuid") UUID uuid) {
        ArquivosModel arquivo = this.arquivosService.buscarArquivoPorUUID(uuid);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(arquivo.getTipo()))
                .body(arquivo.getConteudo());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<byte[]> buscarArquivoPorUsuario(@PathVariable Long idUsuario) {
        ArquivosModel arquivo = this.arquivosService.buscarArquivoPorIdUsuario(idUsuario);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(arquivo.getTipo()))
                .body(arquivo.getConteudo());
    }
}
