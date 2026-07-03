package br.com.api.petpoints.domain.users.cliente.features.meusatendimentos.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.domain.users.cliente.features.meusatendimentos.dto.MeusAtendimentosDto;
import br.com.api.petpoints.domain.users.cliente.features.meusatendimentos.services.MeusAtendimentosClienteServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cliente/atendimentos")
@RequiredArgsConstructor
public class MeusAtendimentosClienteController {

    private final MeusAtendimentosClienteServiceImpl meusAtendimentosClienteService;

    @GetMapping
    public ResponseEntity<List<MeusAtendimentosDto>> listarMeusAtendimentos(HttpServletRequest request) {
        TokenModel tokenModel = new TokenModel(request.getHeader("Authorization"));
        return ResponseEntity.ok().body(this.meusAtendimentosClienteService.listarMeusAtendimentos(tokenModel.getIdUsuario()));
    }
}
