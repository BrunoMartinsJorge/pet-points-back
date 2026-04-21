package br.com.api.petpoints.modules.users.estoquista.features.dashboard.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.modules.users.estoquista.features.dashboard.dto.HistoricoMovimentacoesMensalDto;
import br.com.api.petpoints.modules.users.estoquista.features.dashboard.service.DashboardEstoquistaServiceImpl;
import br.com.api.petpoints.modules.users.estoquista.shared.dto.ProdutoEstoqueDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/estoquista/estoquista-dashboard")
@RequiredArgsConstructor
public class DashboardEstoquistaController {

    private final DashboardEstoquistaServiceImpl dashboardEstoquistaService;

    @GetMapping("/movimentacoes-mensais")
    public ResponseEntity<List<HistoricoMovimentacoesMensalDto>> listarMovimentacoesMensaisParaGrafico(HttpServletRequest request) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok().body(this.dashboardEstoquistaService.listarMovimentacoesMensaisGrafico(token.getIdUsuario()));
    }

    @GetMapping("/produtos-abaixo-estoque")
    public ResponseEntity<List<ProdutoEstoqueDto>> listarProdutosAbaixoEstoque(){
        return ResponseEntity.ok().body(this.dashboardEstoquistaService.listarProdutosAbaixoEstoque());
    }
}
