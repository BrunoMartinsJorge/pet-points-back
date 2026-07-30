package br.com.api.petpoints.domain.auth.controller;

import br.com.api.petpoints.domain.auth.dto.TokenDto;
import br.com.api.petpoints.domain.auth.forms.LoginForm;
import br.com.api.petpoints.shared.form.RegistroForm;
import br.com.api.petpoints.domain.auth.service.UsuarioServiceImpl;
import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.models.ArquivosModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/autenticacao")
@RequiredArgsConstructor
@Tag(name = "Usuário Autenticação Controller", description = "Endpoints para Registrar Clientes, Logar Clientes e Registrar uma nova senha ao usuário") // Groups endpoints
public class UsuarioController {

    private final UsuarioServiceImpl usuarioService;

    @PostMapping("/login")
    @Operation(
            summary = "Loga o usuário por Email e Senha",
            description = "Busca o usuário com base no seu login(email + senha-encriptada) e retorna uma Token JWT"
    )
    @ApiResponse(responseCode = "200", description = "Usuário encontrado")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "405", description = "Usuário com conta desativada")
    public ResponseEntity<Object> logarUsuario(@RequestBody @Valid LoginForm form) {
        return ResponseEntity.ok(usuarioService.logarUsuario(form));
    }

    @PostMapping("/register")
    public ResponseEntity<TokenDto> registrarUsuario(
            @RequestParam String nome,
            @RequestParam GeneroEnum genero,
            @RequestParam String cpf,
            @RequestParam String email,
            @RequestParam String telefone,
            @RequestParam String senha,
            @RequestParam LocalDate dataNascimento,
            @RequestParam(required = false) MultipartFile foto
    ) {
        RegistroForm form = new RegistroForm(
                nome,
                genero,
                cpf,
                email,
                telefone,
                senha,
                dataNascimento
        );
        return ResponseEntity.ok(usuarioService.registrarUsuario(form, foto));
    }

    @GetMapping("/imagem/{id}")
    public ResponseEntity<byte[]> buscarImagem(
            @PathVariable UUID id
    ) {
        ArquivosModel arquivo = this.usuarioService.buscarImagemUsuario(id);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(arquivo.getTipo()))
                .body(arquivo.getConteudo());
    }

    @GetMapping("/enviar-codigo-alterar-senha")
    public void enviarCodigoAlteracaoSenha(@RequestParam String email) {
        this.usuarioService.enviarCodigoAlteracaoSenha(email);
    }

    @PutMapping("/validar-codigo-alterar-senha")
    public ResponseEntity<Boolean> validarCodigoAlterarSenha(@RequestParam String email, @RequestParam String codigo) {
        return ResponseEntity.ok(this.usuarioService.validarCodigoAlterarSenha(email, codigo));
    }

    @PutMapping("/redefinir-senha")
    public void redefinirSenha(@RequestParam String email, @RequestParam String novaSenha, @RequestParam String codigo) {
        this.usuarioService.alterarSenha(email, novaSenha, codigo);
    }
}
