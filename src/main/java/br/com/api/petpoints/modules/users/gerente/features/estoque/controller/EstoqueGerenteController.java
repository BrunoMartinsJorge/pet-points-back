package br.com.api.petpoints.modules.users.gerente.features.estoque.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.modules.users.gerente.features.estoque.dto.ProdutoDto;
import br.com.api.petpoints.modules.users.gerente.features.estoque.form.FiltrosProdutoForm;
import br.com.api.petpoints.modules.users.gerente.features.estoque.service.EstoqueGerenteServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gerente/estoque")
public class EstoqueGerenteController {

    private final EstoqueGerenteServiceImpl estoqueGerenteService;

    @GetMapping
    public ResponseEntity<List<ProdutoDto>> listarProdutos(){
        return ResponseEntity.ok(this.estoqueGerenteService.listarProdutos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDto> buscarProdutoPorId(@PathVariable Long id){
        return ResponseEntity.ok().body(this.estoqueGerenteService.buscarProdutoPorId(id));
    }

    @GetMapping("/estoque-baixo")
    public ResponseEntity<List<ProdutoDto>> listarEstoqueBaixo(){
        return ResponseEntity.ok().body(this.estoqueGerenteService.buscarProdutosComEstoqueAbaixoDoLimite());
    }

    @PutMapping("/gerar-relatorio")
    public ResponseEntity<byte[]> gerarRelatorioProdutosEstoqque(@RequestBody FiltrosProdutoForm form) {
        byte[] pdf = this.estoqueGerenteService.gerarRelatorioProdutos(form);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=RelatorioGenerico.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
