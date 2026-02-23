package br.com.api.petpoints.shared.features.logs;

import br.com.api.petpoints.modules.usuario.models.UsuarioModel;
import br.com.api.petpoints.modules.usuario.repository.UsuarioRepository;
import br.com.api.petpoints.shared.annotations.LogsRegistro;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final LogsService logsService;
    private final UsuarioRepository usuarioRepository;

    @AfterReturning("@annotation(logsRegistro) && args(usuarioEmail)")
    public void registrarLog(LogsRegistro logsRegistro, String usuarioEmail) {

        Optional<UsuarioModel> usuario = this.usuarioRepository.findByEmail(usuarioEmail);

        if (usuario.isEmpty()) throw new ObjectNotFoundException("Usuário não encontrado!");

        logsService.registrarLog(
                usuario.get(),
                logsRegistro.tipo()
        );
    }
}
