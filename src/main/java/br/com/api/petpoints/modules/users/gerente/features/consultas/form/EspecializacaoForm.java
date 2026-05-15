package br.com.api.petpoints.modules.users.gerente.features.consultas.form;

import br.com.api.petpoints.shared.models.EspecializacaoModel;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class EspecializacaoForm {
    @NotEmpty(message = "O campo 'descrição' não pode estar vazio ao adicionar nova especialização!")
    private String descricao;

    public static EspecializacaoModel criarNova(EspecializacaoForm form) {
        EspecializacaoModel model = new EspecializacaoModel();
        model.setDescricao(form.getDescricao());
        return model;
    }
}
