package br.com.api.petpoints.modules.gerente.features.logs.service;

import br.com.api.petpoints.modules.gerente.features.logs.dto.LogDto;
import br.com.api.petpoints.modules.gerente.features.logs.dto.RelatorioLogDto;
import br.com.api.petpoints.modules.gerente.features.logs.dto.UsuariosLogsDto;
import br.com.api.petpoints.modules.gerente.features.logs.forms.RelatorioLogsForm;
import br.com.api.petpoints.shared.enums.TipoLogEnum;
import br.com.api.petpoints.shared.models.LogsModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.LogsRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import br.com.api.petpoints.shared.utils.RelatoriosUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class LogsGerenteServiceImpl implements LogsGerenteService {

    private final LogsRepository logsRepository;
    private final UsuarioRepository usuarioRepository;
    private final TemplateEngine templateEngine;

    private List<LogsModel> getLogs() {
        return logsRepository.findAll();
    }

    @Override
    public List<LogDto> listarLogs() {
        List<LogsModel> logs = this.getLogs();
        return logs.stream().map(LogDto::new).toList();
    }

    @Override
    public List<UsuariosLogsDto> listarUsuariosLogs() {
        List<UsuarioModel> usuarios = this.usuarioRepository.findAll();
        return usuarios.stream().map(UsuariosLogsDto::new).toList();
    }

    @Override
    public byte[] gerarRelatorio(RelatorioLogsForm form) {
        List<RelatorioLogDto> logs = filtrarLogsRelatorio(form, this.getLogs());
        Context context = new Context();
        context.setVariable("logs", logs);
        context.setVariable("dataGeracao", LocalDateTimeUtils.converterLocalDateTimeParaPtBr(LocalDateTime.now()));
        String html = templateEngine.process("relatorios/RelatorioLogs", context);
        return RelatoriosUtils.getBytes(html);
    }

    private List<RelatorioLogDto> filtrarLogsRelatorio(RelatorioLogsForm form, List<LogsModel> logs) {
        Stream<LogsModel> stream = logs.stream();

         if (form.getIdUsuario() != null) {
            stream = stream.filter(log ->
                    log.getLancadoPor() != null &&
                            Objects.equals(log.getLancadoPor().getId(), form.getIdUsuario())
            );
        }

        if (form.getTipo() != null) {
            stream = stream.filter(log ->
                    Objects.equals(log.getTipo(), form.getTipo())
            );
        }

        return RelatorioLogDto.converter(stream.toList());
    }
}
