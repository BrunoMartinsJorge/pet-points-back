package br.com.api.petpoints.shared.features.perfil.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.shared.features.perfil.dto.InformacoesUsuarioDto;
import br.com.api.petpoints.shared.features.perfil.form.EditarPerfilForm;
import br.com.api.petpoints.shared.features.perfil.service.UsuarioPerfilServiceImpl;
import br.com.api.petpoints.shared.enums.GeneroEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class UsuarioPerfilController {

    private final UsuarioPerfilServiceImpl service;

    @GetMapping("/informacoes-usuario")
    public ResponseEntity<InformacoesUsuarioDto> buscarInformacoesUsuario(HttpServletRequest request) {
        TokenModel tokenModel = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok().body(this.service.buscarInformacoes(tokenModel.getIdUsuario()));
    }

    @PutMapping("/editar-perfil")
    public ResponseEntity<Void> editarInformacoes(HttpServletRequest request,
                                                  @RequestParam String nome,
                                                  @RequestParam GeneroEnum genero,
                                                  @RequestParam String email,
                                                  @RequestParam String telefone,
                                                  @RequestParam String cpf,
                                                  @RequestParam LocalDate dataNascimento,
                                                  @RequestParam(required = false) MultipartFile imagem
    ) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        EditarPerfilForm form = new EditarPerfilForm(nome, dataNascimento, genero, email, telefone, cpf);
        this.service.editarInformacoesUsuario(token.getIdUsuario(), form, imagem);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/desativar-perfil")
    public ResponseEntity<Void> desativarPerfilCliente(HttpServletRequest request) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        this.service.desativarPerfil(token.getIdUsuario());
        return ResponseEntity.noContent().build();
    }
}
