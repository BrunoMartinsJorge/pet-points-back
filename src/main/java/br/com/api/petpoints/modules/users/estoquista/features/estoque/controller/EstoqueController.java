package br.com.api.petpoints.modules.users.estoquista.features.estoque.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.modules.users.estoquista.features.estoque.dto.CardsEstoqueDto;
import br.com.api.petpoints.modules.users.estoquista.features.estoque.dto.ProdutoDetalhesDto;
import br.com.api.petpoints.modules.users.estoquista.features.estoque.dto.ProdutoEstoqueDto;
import br.com.api.petpoints.modules.users.estoquista.features.estoque.form.FiltrosProdutoForm;
import br.com.api.petpoints.modules.users.estoquista.features.estoque.form.NovaMovimentacaoForm;
import br.com.api.petpoints.modules.users.estoquista.features.estoque.form.NovoProdutoForm;
import br.com.api.petpoints.modules.users.estoquista.features.estoque.service.EstoqueServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
    public ResponseEntity<ProdutoDetalhesDto> buscarDetalhesProdutoEstoque(HttpServletRequest request, @PathVariable Long idProduto) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok().body(this.estoqueService.buscarDetalhesProdutosEstoque(idProduto, token.getIdUsuario()));
    }

    @PostMapping("/relatorio-produtos")
    public ResponseEntity<byte[]> gerarRelatorioProdutos(FiltrosProdutoForm form){
        byte[] pdf = this.estoqueService.gerarRelatorioProdutos(form);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=RelatorioProdutos.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/adicionar-novo-produto")
    public ResponseEntity<Void> cadastrarNovoProduto(@RequestBody NovoProdutoForm form) {
        this.estoqueService.registrarProduto(form);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/realizar-movimentacao")
    public ResponseEntity<Void> realizarNovaMovimentacao(@Valid @RequestBody NovaMovimentacaoForm form, HttpServletRequest request) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        this.estoqueService.realizarMovimentacao(form, token.getIdUsuario());
        return ResponseEntity.ok().build();
    }
}
