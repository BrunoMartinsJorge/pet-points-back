package br.com.api.petpoints.shared.features.logs;

import br.com.api.petpoints.modules.usuario.models.UsuarioModel;
import br.com.api.petpoints.shared.enums.TipoLogEnum;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogsServiceImpl implements LogsService{
    private final LogRepository logRepository;

    @Override
    @Transactional
    public void registrarLog(UsuarioModel usuario, TipoLogEnum tipoLog) {
        LogModel log = new LogModel(
                usuario,
                tipoLog,
                this.gerarMensagemLog(tipoLog, usuario)
        );
        this.logRepository.save(log);
    }

    private String gerarMensagemLog(TipoLogEnum tipoLog, UsuarioModel usuario) {
        return switch (tipoLog) {
            case LOGIN -> "O usuário " + usuario.getEmail() + " efetuou login!";
            case REGISTRO -> "Um novo usuário foi registrado ao sistema: " + usuario.getEmail();
            default -> "";
        };
    }
}
