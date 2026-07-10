package br.com.api.petpoints.domain.users.gerente.features.funcionarios.controllers;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.shared.dto.AvaliacoesDto;
import br.com.api.petpoints.domain.users.gerente.features.funcionarios.dto.ConsultaFuncionarioDto;
import br.com.api.petpoints.domain.users.gerente.features.funcionarios.dto.FuncionarioDto;
import br.com.api.petpoints.domain.users.gerente.features.funcionarios.dto.MovimentacoesEstoquistasDto;
import br.com.api.petpoints.domain.users.gerente.features.funcionarios.forms.EditarFuncionarioForm;
import br.com.api.petpoints.domain.users.gerente.features.funcionarios.forms.FiltroFuncionariosForm;
import br.com.api.petpoints.domain.users.gerente.features.funcionarios.forms.FuncionarioForm;
import br.com.api.petpoints.domain.users.gerente.features.funcionarios.services.FuncionariosGerenteServiceImpl;
import br.com.api.petpoints.shared.dto.OpcoesDto;
import br.com.api.petpoints.shared.enums.GeneroEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gerente/funcionarios")
public class FuncionariosGerenteController {

    private final FuncionariosGerenteServiceImpl funcionariosGerenteService;

    @GetMapping
    public ResponseEntity<List<FuncionarioDto>> listarFuncionarios(HttpServletRequest request) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok(this.funcionariosGerenteService.listarFuncionarios(token.getIdUsuario()));
    }

    @GetMapping("/especializacoes")
    public ResponseEntity<Set<OpcoesDto>> listarEspecializacoes() {
        return ResponseEntity.ok().body(this.funcionariosGerenteService.listarEspecializacoes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioDto> buscarFuncionarioPorId(@PathVariable Long id) {
        return ResponseEntity.ok().body(this.funcionariosGerenteService.buscarFuncionarioPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioDto> editarFuncionario(@PathVariable Long id, @RequestParam String nome,
                                                            @RequestParam GeneroEnum genero,
                                                            @RequestParam String email,
                                                            @RequestParam String telefone,
                                                            @RequestParam TipoUsuario permissao,
                                                            @RequestParam LocalDate dataNascimento,
                                                            @RequestParam(required = false) MultipartFile foto) {
        EditarFuncionarioForm form = new EditarFuncionarioForm(nome, genero, email, telefone, permissao, dataNascimento);
        return ResponseEntity.ok().body(this.funcionariosGerenteService.atualizarFuncionario(form, id, foto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativarFuncionarioPorId(HttpServletRequest request, @PathVariable Long id) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        this.funcionariosGerenteService.desativarFuncionario(token.getIdUsuario(), id);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<FuncionarioDto> registrarNovoFuncionario(@RequestBody @Valid FuncionarioForm form) {
        return ResponseEntity.ok().body(this.funcionariosGerenteService.cadastrarNovoFuncionario(form));
    }

    @PutMapping("/relatorio")
    public ResponseEntity<byte[]> gerarRelatorioFuncionarios(@Valid @RequestBody FiltroFuncionariosForm form) {
        byte[] pdf = this.funcionariosGerenteService.gerarRelatorioFuncionarios(form);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=RelatorioGenerico.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/avaliacoes/{id}")
    public ResponseEntity<List<AvaliacoesDto>> buscarAvaliacoesFuncionario(@PathVariable Long id) {
        return ResponseEntity.ok().body(this.funcionariosGerenteService.buscarAvaliacoesPorId(id));
    }

    @GetMapping("/consultas/{id}")
    public ResponseEntity<List<ConsultaFuncionarioDto>> buscarConsultasFuncionario(@PathVariable Long id) {
        return ResponseEntity.ok().body(this.funcionariosGerenteService.buscarConsultasPorId(id));
    }

    @GetMapping("/movimentacoes/{id}")
    public ResponseEntity<List<MovimentacoesEstoquistasDto>> buscarMovimentacoesFuncionario(@PathVariable Long id) {
        return ResponseEntity.ok().body(this.funcionariosGerenteService.buscarMovimentacoesPorId(id));
    }
}
