package br.com.api.petpoints.domain.auth.forms;

import lombok.Getter;

@Getter
public class LoginForm {
    //@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$\n" +
            //"\n", message = "Campo 'email' com valor inválido para email!")
    private String email;
    private String senha;
}
