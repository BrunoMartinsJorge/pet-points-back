package br.com.api.petpoints.shared.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_consulta")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoConsultaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O campo 'nome' não pode estar em branco!")
    private String nome;

    @NotBlank(message = "O campo 'descrição' não pode estar em branco!")
    private String descricao;

    @NotNull(message = "O campo 'valor' não pode ser nulo!")
    private double valor;
}
