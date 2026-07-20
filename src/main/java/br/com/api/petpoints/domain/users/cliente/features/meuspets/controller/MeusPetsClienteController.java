package br.com.api.petpoints.domain.users.cliente.features.meuspets.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.domain.users.cliente.features.meuspets.dto.MeuPetConsultaDto;
import br.com.api.petpoints.domain.users.cliente.features.meuspets.dto.MeuPetDto;
import br.com.api.petpoints.domain.users.cliente.features.meuspets.dto.PetPodeSerDeletadoDto;
import br.com.api.petpoints.domain.users.cliente.features.meuspets.forms.EditarPetForm;
import br.com.api.petpoints.domain.users.cliente.features.meuspets.forms.NovoPetForm;
import br.com.api.petpoints.domain.users.cliente.features.meuspets.services.MeusPetsClienteServiceImpl;
import br.com.api.petpoints.domain.users.cliente.features.minhasconsultas.dto.MinhasConsultasDto;
import br.com.api.petpoints.shared.dto.CarteirinhaDto;
import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.enums.TipoAnimalEnum;
import br.com.api.petpoints.shared.models.ArquivosModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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
    public ResponseEntity<MeuPetDto> cadastrarNovoPet(HttpServletRequest request,
                                                      @RequestParam String nome,
                                                      @RequestParam GeneroEnum genero,
                                                      @RequestParam String raca,
                                                      @RequestParam TipoAnimalEnum tipo,
                                                      @RequestParam String observacoes,
                                                      @RequestParam LocalDate dataNascimento,
                                                      @RequestParam(required = false) MultipartFile foto) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        NovoPetForm form = new NovoPetForm(
                nome,
                tipo,
                raca,
                genero,
                dataNascimento,
                observacoes
        );
        return ResponseEntity.ok().body(this.meusPetsClienteService.registrarNovoPet(form, foto, token.getIdUsuario()));
    }

    @PutMapping("/{idPet}")
    public ResponseEntity<MeuPetDto> editarPet(HttpServletRequest request,
                                               @RequestParam String nome,
                                               @RequestParam GeneroEnum genero,
                                               @RequestParam String raca,
                                               @RequestParam TipoAnimalEnum tipo,
                                               @RequestParam String observacoes,
                                               @RequestParam LocalDate dataNascimento,
                                               @RequestParam(required = false) MultipartFile foto,
                                               @PathVariable Long idPet) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        EditarPetForm form = new EditarPetForm(
                nome,
                observacoes,
                genero,
                raca,
                tipo,
                dataNascimento
        );
        this.meusPetsClienteService.editarPet(idPet, token.getIdUsuario(), form, foto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/imagem/{id}")
    public ResponseEntity<byte[]> buscarImagem(
            @PathVariable Long id
    ) {
        ArquivosModel arquivo = this.meusPetsClienteService.buscarImagemPet(id);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(arquivo.getTipo()))
                .body(arquivo.getConteudo());
    }

    @GetMapping(value = "/carteirinha/{idPet}")
    public ResponseEntity<CarteirinhaDto> gerarCarteirinhaDePet(@PathVariable Long idPet, Model model) {
        return ResponseEntity.ok().body(this.meusPetsClienteService.gerarCarteirinha(idPet, model));
    }

    @GetMapping("/baixar-carteirinha/{idPet}")
    public ResponseEntity<byte[]> baixarCarteirinha(@PathVariable Long idPet) {
        byte[] pdf = this.meusPetsClienteService.baixarCarteirinha(idPet);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Carteirinha.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
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

    @GetMapping("/consultas/{idPet}")
    public ResponseEntity<List<MinhasConsultasDto>> buscarConsultasPet(@PathVariable Long idPet) {
        return ResponseEntity.ok().body(this.meusPetsClienteService.listarConsultasPet(idPet));
    }

    @GetMapping("/verificar-pet/{idPet}")
    public ResponseEntity<PetPodeSerDeletadoDto> verificarSePetPodeSerDeletado(HttpServletRequest request, @PathVariable Long idPet) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok().body(this.meusPetsClienteService.verificarPetPodeSerDeletado(idPet, token.getIdUsuario()));
    }

    @DeleteMapping("/{idPet}")
    public ResponseEntity<Void> deletarPet(HttpServletRequest request, @PathVariable Long idPet) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        this.meusPetsClienteService.deletarPet(idPet, token.getIdUsuario());
        return ResponseEntity.noContent().build();
    }
}
