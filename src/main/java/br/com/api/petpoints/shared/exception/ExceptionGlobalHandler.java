package br.com.api.petpoints.shared.exception;

import br.com.api.petpoints.modules.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.exception.custom.PerfilDesativadoException;
import br.com.api.petpoints.shared.exception.custom.StandardException;
import br.com.api.petpoints.shared.exception.custom.TokenExpiradaException;
import br.com.api.petpoints.shared.features.logs.LogsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionGlobalHandler {

    private final LogsServiceImpl logsService;

    @ExceptionHandler(TokenExpiradaException.class)
    public ResponseEntity<StandardException> TokenExpirada(TokenExpiradaException ex, HttpServletRequest request) {
        StandardException error = new StandardException(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                request.getRequestURI()
        );
        logsService.registrarException(ex, request, HttpStatus.UNAUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(UsuarioNaoEncontrado.class)
    public ResponseEntity<?> usuarioNaoEncontrado(UsuarioNaoEncontrado ex, HttpServletRequest request) {
        StandardException error = new StandardException(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getRequestURI()
        );
        logsService.registrarException(ex, request, HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (ex instanceof ResponseStatusException responseStatusException) {
            status = (HttpStatus) responseStatusException.getStatusCode();
        }

        logsService.registrarException(ex, request, status);
        StandardException error = new StandardException(
                LocalDateTime.now(),
                HttpStatus.BAD_GATEWAY.value(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }

    @ExceptionHandler(PerfilDesativadoException.class)
    public ResponseEntity<StandardException> perfilDesativado(PerfilDesativadoException ex, HttpServletRequest request){
        StandardException error = new StandardException(
                LocalDateTime.now(),
                HttpStatus.BAD_GATEWAY.value(),
                ex.getMessage(),
                request.getRequestURI()
        );
        logsService.registrarException(ex, request, HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<StandardException> ObjectNotFound(ObjectNotFoundException ex, HttpServletRequest request) {
        StandardException error = new StandardException(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getRequestURI()
        );
        logsService.registrarException(ex, request, HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

}
