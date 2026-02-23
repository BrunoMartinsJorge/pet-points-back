package br.com.api.petpoints.shared.annotations;

import br.com.api.petpoints.shared.enums.TipoLogEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogsRegistro {
    TipoLogEnum tipo();
}
