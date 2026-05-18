package br.com.api.petpoints.modules.auth.controller;

import br.com.api.petpoints.modules.auth.dto.TokenDto;
import br.com.api.petpoints.modules.auth.forms.LoginForm;
import br.com.api.petpoints.modules.auth.forms.RegistroForm;
import br.com.api.petpoints.modules.auth.service.UsuarioServiceImpl;
import br.com.api.petpoints.shared.enums.GeneroEnum;
import br.com.api.petpoints.shared.models.ArquivosModel;
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
public class UsuarioController {

    private final UsuarioServiceImpl usuarioService;

    @PostMapping("/login")
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
