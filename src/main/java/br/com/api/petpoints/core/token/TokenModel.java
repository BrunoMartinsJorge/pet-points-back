package br.com.api.petpoints.core.token;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
//@NoArgsConstructor
@Data
public class TokenModel {
    private String token;
    private Long idUsuario;
    private String rule;
    //private List<String> acessos = new ArrayList<>();
    private String nomeUsuario;

    public TokenModel(String token) {
        DecodedJWT decod = TokenService.converterToken(token);
        TokenModel tokenDecoded = TokenService.gerarTokenModel(decod);
        this.token = tokenDecoded.getToken();
        this.idUsuario = tokenDecoded.getIdUsuario();
        this.rule = tokenDecoded.getRule();
        // this.acessos = tokenDecoded.getAcessos();
        this.nomeUsuario = tokenDecoded.getNomeUsuario();
    }
}
