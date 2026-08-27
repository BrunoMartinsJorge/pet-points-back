package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto;

import br.com.api.petpoints.shared.models.PetModel;
import br.com.api.petpoints.shared.models.PrescricaoModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PrescrisaoDto {
    private Long numero;
    private VeterinarioPrescrisaoDto veterinario;
    private PetPrescrisaoDto animal;
    private TutorPrescrisaoDto tutor;
    private String diagnostico;
    private String orientacoes;
    private String dataReavaliacao;
    private String local;
    private String data;

    public PrescrisaoDto(
            PrescricaoModel prescricao,
            UsuarioModel veterinario,
            UsuarioModel tutor,
            PetModel pet,
            String diagnostico,
            String localClinica,
            String dataRetorno
    ) {
        this.numero = prescricao.getId();
        this.veterinario = new VeterinarioPrescrisaoDto(veterinario);
        this.tutor = new TutorPrescrisaoDto(tutor);
        this.animal = new PetPrescrisaoDto(pet);
        this.diagnostico = diagnostico;
        this.orientacoes = prescricao.getOrientacoesGerais();
        this.dataReavaliacao = dataRetorno;
        this.local = localClinica;
        this.data = LocalDateTimeUtils.converterLocalDateTimeParaPtBr(LocalDateTime.now());
    }
}
