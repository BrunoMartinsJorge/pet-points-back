package br.com.api.petpoints.shared.features.logs;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.core.token.TokenService;
import br.com.api.petpoints.modules.usuario.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.shared.exception.custom.TokenNaoEncontradaException;
import br.com.api.petpoints.shared.models.LogsModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.enums.TipoLogEnum;
import br.com.api.petpoints.shared.repository.LogsRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogsServiceImpl implements LogsService {
    private final LogsRepository logRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public void registrarLog(UsuarioModel usuario, TipoLogEnum tipoLog) {
        LogsModel log = new LogsModel(
                usuario,
                tipoLog,
                this.gerarMensagemLog(tipoLog, usuario)
        );
        this.logRepository.save(log);
    }

    @Override
    public void registrarException(Exception ex, HttpServletRequest request, HttpStatus status) {

        UsuarioModel usuario = this.buscarUsuario(request);

        LogsModel log = new LogsModel(
                usuario,
                TipoLogEnum.ERRO,
                ex.getClass().getSimpleName()
        );

        this.logRepository.save(log);
    }

    private String gerarMensagemLog(TipoLogEnum tipoLog, UsuarioModel usuario) {
        return switch (tipoLog) {
            case LOGIN -> "O usuário " + usuario.getEmail() + " efetuou login!";
            case REGISTRO -> "Um novo usuário foi registrado ao sistema: " + usuario.getEmail();
            case CANCELOU_CONSULTA -> "O usuário " + usuario.getNome() + " - " + usuario.getEmail() + " - " + usuario.getPermissao() + ". Cancelou uma consulta!";
            default -> "";
        };
    }

    private UsuarioModel buscarUsuario(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) return null;
        TokenModel tokenDecoded = TokenService.converterTokenParaModel(request.getHeader("Authorization"));
        Optional<UsuarioModel> usuario = this.usuarioRepository.findById(tokenDecoded.getIdUsuario());
        if (usuario.isEmpty()) throw new UsuarioNaoEncontrado("Usuário não encontrado com ID: " + tokenDecoded.getIdUsuario() + "!");
        return usuario.get();
    }
}
