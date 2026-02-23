package br.com.api.petpoints.modules.usuario.exception;

import br.com.api.petpoints.shared.exception.custom.StandardException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class UsuariosExceptions {

    @ExceptionHandler(UsuarioNaoEncontrado.class)
    public ResponseEntity<StandardException> ClienteNaoEncontrado(UsuarioNaoEncontrado ex, HttpServletRequest request) {
        StandardException error = new StandardException(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UsuarioJaCadastrado.class)
    public ResponseEntity<StandardException> ClienteJaCadastrado(UsuarioJaCadastrado ex, HttpServletRequest request) {
        StandardException error = new StandardException(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
