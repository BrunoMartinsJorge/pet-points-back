package br.com.api.petpoints.shared.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum TipoAnimalEnum implements Serializable {
    CACHORRO("Cachorro"),
    GATO("Gato"),
    PASSARO("Pássaro"),
    COELHO("Coelho"),
    HAMSTER("Hamster"),
    PEIXE("Peixe");

    private final String descricao;
    TipoAnimalEnum(String descricao) {
        this.descricao = descricao;
    }
}
