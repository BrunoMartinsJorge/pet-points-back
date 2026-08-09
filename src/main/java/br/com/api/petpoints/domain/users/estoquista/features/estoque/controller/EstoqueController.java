package br.com.api.petpoints.domain.users.estoquista.features.estoque.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.domain.users.estoquista.features.estoque.dto.CardsEstoqueDto;
import br.com.api.petpoints.domain.users.estoquista.features.estoque.dto.ProdutoDetalhesDto;
import br.com.api.petpoints.domain.users.estoquista.features.estoque.form.EditarProdutoForm;
import br.com.api.petpoints.domain.users.estoquista.shared.dto.ProdutoEstoqueDto;
import br.com.api.petpoints.shared.form.FiltrosProdutoForm;
import br.com.api.petpoints.domain.users.estoquista.features.estoque.form.NovoProdutoForm;
import br.com.api.petpoints.domain.users.estoquista.features.estoque.service.EstoqueServiceImpl;
import br.com.api.petpoints.shared.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estoquista/estoque")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueServiceImpl estoqueService;

    @GetMapping("/informacoes-card")
    public ResponseEntity<CardsEstoqueDto> buscarDadosCards() {
        return ResponseEntity.ok(this.estoqueService.gerarCardsEstoque());
    }

    @GetMapping("/listar-produtos")
    public ResponseEntity<List<ProdutoEstoqueDto>> listarProdutosEstoque() {
        return ResponseEntity.ok(this.estoqueService.listarProdutosEstoque());
    }

    @GetMapping("/detalhes-produto/{idProduto}")
    public ResponseEntity<ProdutoDetalhesDto> buscarDetalhesProdutoEstoque(@PathVariable Long idProduto) {
        return ResponseEntity.ok().body(this.estoqueService.buscarDetalhesProdutosEstoque(idProduto));
    }

    @PostMapping("/relatorio-produtos")
    public ResponseEntity<byte[]> gerarRelatorioProdutos(@RequestBody FiltrosProdutoForm form) {
        byte[] pdf = this.estoqueService.gerarRelatorioProdutos(form);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=RelatorioProdutos.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/adicionar-novo-produto")
    public ResponseEntity<Void> cadastrarNovoProduto(HttpServletRequest request, @RequestBody NovoProdutoForm form) {
        this.estoqueService.registrarProduto(TokenUtils.getIdUsuario(request), form);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/editar-produto/{idProduto}")
    public ResponseEntity<Void> editarProduto(HttpServletRequest request, @PathVariable Long idProduto, @RequestBody EditarProdutoForm form) {
        this.estoqueService.editarProduto(TokenUtils.getIdUsuario(request), form, idProduto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remover-produto/{idProduto}")
    public ResponseEntity<Void> removerProduto(HttpServletRequest request, @PathVariable Long idProduto) {
        this.estoqueService.removerProduto(TokenUtils.getIdUsuario(request), idProduto);
        return ResponseEntity.noContent().build();
    }
}
