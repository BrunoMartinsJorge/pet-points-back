package br.com.api.petpoints.domain.users.gerente.features.pets.controller;

import br.com.api.petpoints.domain.users.gerente.features.pets.dto.*;
import br.com.api.petpoints.domain.users.gerente.features.pets.form.RelatorioPetsClinicaForm;
import br.com.api.petpoints.domain.users.gerente.features.pets.service.PetsClinicaServiceImpl;
import br.com.api.petpoints.shared.dto.CarteirinhaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gerente-atendente/pets-clinica")
@RequiredArgsConstructor
public class PetsClinicaController {

    private final PetsClinicaServiceImpl petsClinicaService;

    @GetMapping
    public ResponseEntity<List<PetsDto>> listarPetsClinica() {
        return ResponseEntity.ok().body(this.petsClinicaService.buscarPetsClinica());
    }

    @GetMapping("/tutores")
    public ResponseEntity<List<TutorDto>> listarTutoresFiltros() {
        return ResponseEntity.ok().body(this.petsClinicaService.buscarTutorsClinica());
    }

    @GetMapping(value = "/carteirinha/{idPet}")
    public ResponseEntity<CarteirinhaDto> gerarCarteirinhaDePet(@PathVariable Long idPet, Model model) {
        return ResponseEntity.ok().body(this.petsClinicaService.gerarCarteirinha(idPet, model));
    }

    @GetMapping("/baixar-carteirinha/{idPet}")
    public ResponseEntity<byte[]> baixarCarteirinha(@PathVariable Long idPet) {
        byte[] pdf = this.petsClinicaService.baixarCarteirinha(idPet);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Carteirinha.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/detalhes-pet/{idPet}")
    public ResponseEntity<DetalhesPetDto> buscarDetalhesPet(@PathVariable Long idPet) {
        return ResponseEntity.ok().body(this.petsClinicaService.buscarDetalhesPetsClinica(idPet));
    }

    @GetMapping("/detalhes-tutor/{idPet}")
    public ResponseEntity<DetalhesTutorPetDto> buscarDetalhesTutor(@PathVariable Long idPet) {
        return ResponseEntity.ok().body(this.petsClinicaService.buscarDetalhesTutorsClinica(idPet));
    }

    @GetMapping("/historico-consultas/{idPet}")
    public ResponseEntity<List<HistoricoConsultasPetDto>> buscarHistoricoConsultasPet(@PathVariable Long idPet) {
        return ResponseEntity.ok().body(this.petsClinicaService.buscarHistoricoConsultasPet(idPet));
    }

    @PutMapping("/relatorio")
    public ResponseEntity<byte[]> gerarRelatorioPetsClinica(@RequestBody RelatorioPetsClinicaForm form) {
        byte[] pdf = this.petsClinicaService.gerarRelatorio(form);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=RelatorioGenerico.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
