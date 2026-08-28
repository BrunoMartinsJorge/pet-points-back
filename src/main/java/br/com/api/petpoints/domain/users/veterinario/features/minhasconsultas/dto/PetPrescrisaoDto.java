package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto;

import br.com.api.petpoints.shared.models.PetModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PetPrescrisaoDto {
    private String nome;
    private String especie;
    private String raca;
    private String sexo;
    private String idade;

    public PetPrescrisaoDto(PetModel pet) {
        this.nome = pet.getNome();
        this.especie = pet.getTipo().getDescricao();
        this.raca = pet.getRaca();
        this.sexo = pet.getGenero().getDescricao();
        this.idade = this.calcularIdade(pet.getDataNascimento());
    }

    public String calcularIdade(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            return null;
        }

        LocalDate hoje = LocalDate.now();

        if (dataNascimento.isAfter(hoje)) {
            throw new IllegalArgumentException("A data de nascimento não pode ser futura.");
        }

        Period periodo = Period.between(dataNascimento, hoje);

        if (periodo.getYears() > 0) {
            return periodo.getYears() + (periodo.getYears() == 1 ? " ano" : " anos");
        }

        if (periodo.getMonths() > 0) {
            return periodo.getMonths() + (periodo.getMonths() == 1 ? " mês" : " meses");
        }

        return periodo.getDays() + (periodo.getDays() == 1 ? " dia" : " dias");
    }
}
