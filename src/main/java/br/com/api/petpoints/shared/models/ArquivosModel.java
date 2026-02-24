package br.com.api.petpoints.shared.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "arquivos")
public class ArquivosModel {

    @Id
    @GeneratedValue
    private UUID id;

    private String nomeOriginal;
    private String tipo;
    private Long tamanho;

    @Lob
    @Column(name = "dados")
    private byte[] dados;
}
