package br.com.api.petpoints.core.token;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum TipoUsuario implements Serializable {
    C("CLIENTE"),
    G("GERENTE"),
    E("ESTOQUISTA"),
    V("VETERINARIO"),
    A("ATENDENTE");

    private final String descricao;

    TipoUsuario(String descricao) {
        this.descricao = descricao;
    }
}
