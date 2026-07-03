package br.com.api.petpoints.domain.users.gerente.features.clientes.controller;

import br.com.api.petpoints.domain.users.gerente.features.clientes.dto.ClienteDto;
import br.com.api.petpoints.domain.users.gerente.features.clientes.dto.ClientesDetalhesDto;
import br.com.api.petpoints.domain.users.gerente.features.clientes.dto.HistoricoConsultasClienteDto;
import br.com.api.petpoints.domain.users.gerente.features.clientes.dto.PetsClienteDto;
import br.com.api.petpoints.domain.users.gerente.features.clientes.service.ClientesClinicaServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/gerente/clientes-clinica")
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
}
