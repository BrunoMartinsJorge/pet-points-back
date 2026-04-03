package br.com.api.petpoints.modules.usuario.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.modules.usuario.dto.TokenDto;
import br.com.api.petpoints.modules.usuario.forms.LoginForm;
import br.com.api.petpoints.modules.usuario.forms.RegistroForm;
import br.com.api.petpoints.modules.usuario.service.UsuarioServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/autenticacao")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioServiceImpl usuarioService;

    @PostMapping("/login")
    public ResponseEntity<Object> logarUsuario(@RequestBody @Valid LoginForm form){
        return ResponseEntity.ok(usuarioService.logarUsuario(form));
    }

    @PostMapping("/register")
    public ResponseEntity<TokenDto> registrarUsuario(@RequestBody @Valid RegistroForm form){
        return ResponseEntity.ok(usuarioService.registrarUsuario(form));
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
