package br.com.api.petpoints.shared.exception;

import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.exception.custom.StandardException;
import br.com.api.petpoints.shared.exception.custom.TokenExpirada;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionGlobalHandler {

    @ExceptionHandler(TokenExpirada.class)
    public ResponseEntity<StandardException> TokenExpirada(TokenExpirada ex, HttpServletRequest request) {
        StandardException error = new StandardException(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<StandardException> ObjectNotFound(ObjectNotFoundException ex, HttpServletRequest request) {
        StandardException error = new StandardException(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

}
