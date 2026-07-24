package br.com.api.petpoints.shared.features.clientes.controller;

import br.com.api.petpoints.domain.users.gerente.features.pets.form.RelatorioPetsClinicaForm;
import br.com.api.petpoints.shared.features.clientes.dto.ClienteDto;
import br.com.api.petpoints.shared.features.clientes.dto.ClientesDetalhesDto;
import br.com.api.petpoints.shared.features.clientes.dto.HistoricoConsultasClienteDto;
import br.com.api.petpoints.shared.features.clientes.dto.PetsClienteDto;
import br.com.api.petpoints.shared.features.clientes.forms.RelatorioClienteClinicaForm;
import br.com.api.petpoints.shared.features.clientes.service.ClientesClinicaServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gerente-atendente/clientes-clinica")
@RequiredArgsConstructor
public class ClientesClinicaController {

    private final ClientesClinicaServiceImpl clientesClinicaService;

    @GetMapping
    public ResponseEntity<List<ClienteDto>> listarClientes(){
        return ResponseEntity.ok().body(this.clientesClinicaService.listarClientesClinica());
    }

    @GetMapping("/detalhes/{idCliente}")
    public ResponseEntity<ClientesDetalhesDto> buscarDetalhesClientePorId(@PathVariable Long idCliente) {
        return ResponseEntity.ok().body(this.clientesClinicaService.buscarDetalhesCliente(idCliente));
    }

    @GetMapping("/historico-consultas/{idCliente}")
    public ResponseEntity<List<HistoricoConsultasClienteDto>> buscarHistoricoConsultasClientePorId(@PathVariable Long idCliente) {
        return ResponseEntity.ok().body(this.clientesClinicaService.historicoConsultasCliente(idCliente));
    }

    @GetMapping("/pets-cliente/{idCliente}")
    public ResponseEntity<List<PetsClienteDto>> buscarPetsClientePorId(@PathVariable Long idCliente) {
        return ResponseEntity.ok().body(this.clientesClinicaService.listarPetsCliente(idCliente));
    }

    @PostMapping("/relatorios")
    public ResponseEntity<byte[]> gerarRelatorioClientesClinica(@RequestBody RelatorioClienteClinicaForm form) {
        byte[] pdf = this.clientesClinicaService.gerarRelatorio(form);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=RelatorioGenerico.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
