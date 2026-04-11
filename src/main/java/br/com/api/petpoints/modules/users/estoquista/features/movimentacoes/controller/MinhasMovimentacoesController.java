package br.com.api.petpoints.modules.users.estoquista.features.movimentacoes.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.modules.users.estoquista.features.movimentacoes.dto.MinhasMovimentacoesDto;
import br.com.api.petpoints.modules.users.estoquista.features.movimentacoes.service.MinhasMovimentacoesServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
