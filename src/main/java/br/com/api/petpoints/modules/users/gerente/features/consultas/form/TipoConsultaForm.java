package br.com.api.petpoints.modules.users.gerente.features.consultas.form;

import br.com.api.petpoints.shared.models.TipoConsultaModel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TipoConsultaForm {
    @NotEmpty(message = "O campo 'nome' não pode ser vazio no formulário de edição ou cadastro de tipo de consulta!")
    private String nome;
    @NotEmpty(message = "O campo 'descricao' não pode ser vazio no formulário de edição ou cadastro de tipo de consulta!")
    private String descricao;
    @NotNull
    @Min(value = 0, message = "O campo 'valor' deve possuir valor maior ou equivalente a 0.00!")
    private Double valor;

    public static TipoConsultaModel criarNovo(TipoConsultaForm form) {
        TipoConsultaModel model = new TipoConsultaModel();
        model.setNome(form.getNome());
        model.setDescricao(form.getDescricao());
        model.setValor(form.getValor());
        return model;
    }
}
