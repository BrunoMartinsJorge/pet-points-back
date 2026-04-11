package br.com.api.petpoints.modules.users.gerente.features.estoque.controller;

import br.com.api.petpoints.modules.users.gerente.features.estoque.dto.ProdutoDto;
import br.com.api.petpoints.modules.users.gerente.features.estoque.service.EstoqueGerenteServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
