package br.com.api.petpoints.shared.utils;

import br.com.api.petpoints.shared.models.MovimentacaoModel;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RelatoriosUtils {
    private final TemplateEngine templateEngine;

    public static byte[] getBytes(String html) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html, null);
        builder.toStream(outputStream);
        try {
            builder.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    public byte[] gerarRelatorioGenerico(List<ColunaRelatorio> colunasRelatorios, List<?> data, String titulo, String subtitulo) {
        Context context = new Context();
        context.setVariable("titulo", titulo);
        context.setVariable("subtitulo", subtitulo);
        context.setVariable("dataGeracao", LocalDateTimeUtils.converterLocalDateTimeParaPtBr(LocalDateTime.now()));
        context.setVariable("colunas", colunasRelatorios);
        context.setVariable("dados", data);
        String html = this.templateEngine.process("relatorios/RelatorioGenerico", context);
        return getBytes(html);
    }
}
