package br.com.api.petpoints.domain.users.estoquista.features.movimentacoes.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.domain.users.estoquista.features.movimentacoes.dto.MinhasMovimentacoesDto;
import br.com.api.petpoints.shared.dto.ProdutoFiltroDto;
import br.com.api.petpoints.domain.users.estoquista.features.movimentacoes.form.NovaMovimentacaoForm;
import br.com.api.petpoints.domain.users.estoquista.features.movimentacoes.form.RelatorioMovimentacoesForm;
import br.com.api.petpoints.domain.users.estoquista.features.movimentacoes.service.MinhasMovimentacoesServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estoquista/minhas-movimentacoes")
@RequiredArgsConstructor
public class MinhasMovimentacoesController {

    private final MinhasMovimentacoesServiceImpl minhasMovimentacoesService;

    @GetMapping
    public ResponseEntity<List<MinhasMovimentacoesDto>> listarMovimentacoesPorEstoquista(HttpServletRequest request) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok().body(this.minhasMovimentacoesService.listarMovimentacoesEstoquista(token.getIdUsuario()));
    }

    @GetMapping("/produtos-filtro")
    public ResponseEntity<List<ProdutoFiltroDto>> buscarProdutosParaFiltro() {
        return ResponseEntity.ok().body(this.minhasMovimentacoesService.buscarProdutosParaFiltro());
    }

    @PutMapping("/relatorio-movimentacoes")
    public ResponseEntity<byte[]> gerarRelatorioMovimentacoes(@RequestBody RelatorioMovimentacoesForm form, HttpServletRequest request) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        byte[] pdf = this.minhasMovimentacoesService.gerarRelatorioMovimentacoes(token.getIdUsuario(), form);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=RelatorioGenerico.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PutMapping("/realizar-movimentacao")
    public ResponseEntity<Void> realizarNovaMovimentacao(@Valid @RequestBody NovaMovimentacaoForm form, HttpServletRequest request) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        this.minhasMovimentacoesService.realizarMovimentacao(form, token.getIdUsuario());
        return ResponseEntity.ok().build();
    }
}
