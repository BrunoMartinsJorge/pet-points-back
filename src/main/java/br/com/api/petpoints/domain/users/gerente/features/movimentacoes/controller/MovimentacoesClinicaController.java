package br.com.api.petpoints.domain.users.gerente.features.movimentacoes.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.domain.users.gerente.features.movimentacoes.dto.LancadoPorDto;
import br.com.api.petpoints.domain.users.gerente.features.movimentacoes.dto.MovimentacoesDto;
import br.com.api.petpoints.domain.users.gerente.features.movimentacoes.form.FiltroMovimentacoesForm;
import br.com.api.petpoints.domain.users.gerente.features.movimentacoes.service.MovimentacoesClinicaServiceImpl;
import br.com.api.petpoints.shared.dto.ProdutoFiltroDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gerente/movimentacoes-clinica")
@RequiredArgsConstructor
public class MovimentacoesClinicaController {

    private final MovimentacoesClinicaServiceImpl movimentacoesClinicaService;

    @GetMapping
    public ResponseEntity<List<MovimentacoesDto>> listarMovimentacoesDaClinica() {
        return ResponseEntity.ok().body(this.movimentacoesClinicaService.listarMovimentacoes());
    }

    @GetMapping("/produtos-filtro")
    public ResponseEntity<List<ProdutoFiltroDto>> listarProdutosParaFiltro(){
        return ResponseEntity.ok().body(this.movimentacoesClinicaService.listarProdutosFiltro());
    }

    @GetMapping("/estoquistas-filtro")
    public ResponseEntity<List<LancadoPorDto>> listarEstoquistasParaFiltro(){
        return ResponseEntity.ok().body(this.movimentacoesClinicaService.listarEstoquistasFiltro());
    }

    @PutMapping("/gerar-relatorio")
    public ResponseEntity<byte[]> gerarRelatorioMovimentacoes(HttpServletRequest request, @Valid @RequestBody FiltroMovimentacoesForm form){
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        byte[] pdf = this.movimentacoesClinicaService.gerarRelatorioMovimentacoes(token.getIdUsuario(), form);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=RelatorioGenerico.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
