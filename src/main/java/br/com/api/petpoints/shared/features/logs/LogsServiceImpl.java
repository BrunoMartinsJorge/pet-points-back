package br.com.api.petpoints.shared.features.logs;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.core.token.TokenService;
import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
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
    @Transactional
    public void registrarLog(UsuarioModel usuario, TipoLogEnum tipoLog, String apendice) {
        LogsModel log = new LogsModel(
                usuario,
                tipoLog,
                this.gerarMensagemLog(tipoLog, usuario) + apendice
        );
        this.logRepository.save(log);
    }

    @Override
    @Transactional
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
            case ERRO ->
                    "Ocorreu um erro durante uma ação do usuário " + usuario.getNome() + " - " + usuario.getEmail() + "!";
            case MOVIMENTACAO_ENTRADA ->
                    "O usuário " + usuario.getNome() + " - " + usuario.getEmail() + " registrou uma movimentação de entrada!";
            case MOVIMENTACAO_SAIDA ->
                    "O usuário " + usuario.getNome() + " - " + usuario.getEmail() + " registrou uma movimentação de saída!";
            case SE_DESATIVOU -> "O usuário " + usuario.getNome() + " - " + usuario.getEmail() + " desativou o próprio perfil!";
            case CANCELOU_CONSULTA ->
                    "O usuário " + usuario.getNome() + " - " + usuario.getEmail() + " - " + usuario.getPermissao() + ". Cancelou uma consulta!";
            case SOLICITOU_CONSULTA ->
                    "O usuário " + usuario.getNome() + " - " + usuario.getEmail() + " solicitou uma nova consulta!";
            case DEFERIU_CONSULTA ->
                    "O usuário " + usuario.getNome() + " - " + usuario.getEmail() + " - " + usuario.getPermissao() + ". Deferiu uma solicitação de consulta!";
            case INDEFERIU_CONSULTA ->
                    "O usuário " + usuario.getNome() + " - " + usuario.getEmail() + " - " + usuario.getPermissao() + ". Indeferiu uma solicitação de consulta!";
            case CONSULTA_INICIADA ->
                    "O usuário " + usuario.getNome() + " - " + usuario.getEmail() + " iniciou uma consulta!";
            case CONSULTA_FINALIZADA ->
                    "O usuário " + usuario.getNome() + " - " + usuario.getEmail() + " finalizou uma consulta!";
            case DESATIVOU_PERFIL ->
                    "O usuário " + usuario.getNome() + " - " + usuario.getEmail() + " - " + usuario.getPermissao() + ". Desativou o perfil de outro usuário!";
            case EDITOU_TIPO_CONSULTA ->
                    "O usuário " + usuario.getNome() + " - " + usuario.getEmail() + " - " + usuario.getPermissao() + ". Editou as informações de um tipo de consulta!";
            case ADICIONOU_CLIENTE -> usuario.getNome() + " registrou um novo cliente.";
            case REGISTROU_PRODUTO ->
                    "O usuário " + usuario.getNome() + " - " + usuario.getEmail() + " registrou um novo produto!";
            case REMOVEU_PRODUTO ->
                    "O usuário " + usuario.getNome() + " - " + usuario.getEmail() + " removeu um produto!";
            case EDITOU_PRODUTO ->
                    "O usuário " + usuario.getNome() + " - " + usuario.getEmail() + " editou as informações de um produto!";
            case REGISTROU_PAGAMENTO_PRESENCIAL ->
                    "O usuário " + usuario.getNome() + " - " + usuario.getEmail() + " registrou um pagamento presencial!";
            default -> "Ação Efetuada - Não Registrada";
        };
    }

    private UsuarioModel buscarUsuario(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) return null;
        TokenModel tokenDecoded = TokenService.converterTokenParaModel(request.getHeader("Authorization"));
        Optional<UsuarioModel> usuario = this.usuarioRepository.findById(tokenDecoded.getIdUsuario());
        if (usuario.isEmpty())
            throw new UsuarioNaoEncontrado("Usuário não encontrado com ID: " + tokenDecoded.getIdUsuario() + "!");
        return usuario.get();
    }
}
