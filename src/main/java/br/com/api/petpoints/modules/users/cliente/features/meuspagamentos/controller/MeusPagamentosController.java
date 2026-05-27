package br.com.api.petpoints.modules.users.cliente.features.meuspagamentos.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.modules.users.cliente.features.meuspagamentos.dto.PagamentosPendentesDto;
import br.com.api.petpoints.modules.users.cliente.features.meuspagamentos.service.MeusPagamentosServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cliente/pagamentos")
@RequiredArgsConstructor
public class MeusPagamentosController {

    private final MeusPagamentosServiceImpl meusPagamentosService;

    @GetMapping("/pendentes")
    public ResponseEntity<List<PagamentosPendentesDto>> listarPagamentosPendentesPorCliente(HttpServletRequest request) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok().body(this.meusPagamentosService.listarPagamentosPendentes(token.getIdUsuario()));
    }
}
