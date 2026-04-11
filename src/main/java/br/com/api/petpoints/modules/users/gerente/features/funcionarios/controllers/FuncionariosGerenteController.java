package br.com.api.petpoints.modules.users.gerente.features.funcionarios.controllers;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.modules.users.gerente.features.funcionarios.dto.FuncionarioDto;
import br.com.api.petpoints.modules.users.gerente.features.funcionarios.forms.FuncionarioForm;
import br.com.api.petpoints.modules.users.gerente.features.funcionarios.services.FuncionariosGerenteServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gerente/funcionarios")
public class FuncionariosGerenteController {

    private final FuncionariosGerenteServiceImpl funcionariosGerenteService;

    @GetMapping
    public ResponseEntity<List<FuncionarioDto>> listarFuncionarios(HttpServletRequest request){
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok(this.funcionariosGerenteService.listarFuncionarios(token.getIdUsuario()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioDto> buscarFuncionarioPorId(@PathVariable Long id){
        return ResponseEntity.ok().body(this.funcionariosGerenteService.buscarFuncionarioPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioDto> atualizarDadosDeFuncionarioPorId(@PathVariable Long id, @RequestBody @Valid FuncionarioForm form){
        return ResponseEntity.ok().body(this.funcionariosGerenteService.atualizarFuncionario(form, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativarFuncionarioPorId(HttpServletRequest request, @PathVariable Long id){
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        this.funcionariosGerenteService.desativarFuncionario(token.getIdUsuario(), id);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<FuncionarioDto> registrarNovoFuncionario(@RequestBody @Valid FuncionarioForm form){
        return ResponseEntity.ok().body(this.funcionariosGerenteService.cadastrarNovoFuncionario(form));
    }
}
