package br.com.api.petpoints.modules.cliente.features.meuspets.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.modules.cliente.features.meuspets.dto.MeuPetConsultaDto;
import br.com.api.petpoints.modules.cliente.features.meuspets.dto.MeuPetDto;
import br.com.api.petpoints.modules.cliente.features.meuspets.forms.NovoPetForm;
import br.com.api.petpoints.modules.cliente.features.meuspets.services.MeusPetsClienteServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cliente/meus-pets")
@RequiredArgsConstructor
public class MeusPetsClienteController {

    private final MeusPetsClienteServiceImpl meusPetsClienteService;

    @GetMapping
    public ResponseEntity<List<MeuPetDto>> listarMeusPets(HttpServletRequest request) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok().body(this.meusPetsClienteService.listarMeusPets(token.getIdUsuario()));
    }

    @PostMapping
    public ResponseEntity<MeuPetDto> cadastrarNovoPet(HttpServletRequest request, @Valid @RequestBody NovoPetForm form) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok().body(this.meusPetsClienteService.registrarNovoPet(form, token.getIdUsuario()));
    }

    @GetMapping("/carteirinha/{idPet}")
    public String gerarCarteirinhaDePet(@PathVariable Long idPet, Model model) {
        return this.meusPetsClienteService.gerarCarteirinha(idPet, model);
    }

    @GetMapping("/pets-consultas")
    public ResponseEntity<List<MeuPetConsultaDto>> listarMeusPetsComConsulta(HttpServletRequest request) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok().body(this.meusPetsClienteService.listarPetsConsultas(token.getIdUsuario()));
    }

    @GetMapping("/{idPet}")
    public ResponseEntity<?> buscarInformacoesPet(HttpServletRequest request, @PathVariable Long idPet) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok().body(this.meusPetsClienteService.buscarInformacoesDePet(token.getIdUsuario(), idPet));
    }
}
