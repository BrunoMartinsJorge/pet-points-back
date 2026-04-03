package br.com.api.petpoints.shared.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class LocalDateTimeUtils {

    public static String converterLocalDateTimeParaPtBr(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        LocalDate data = localDateTime.toLocalDate();
        LocalTime horario = localDateTime.toLocalTime();
        return data.getDayOfMonth() + "/" + data.getMonthValue() + "/" + data.getYear() + " - " + horario.getHour() + ":" + horario.getMinute() + ":" + horario.getSecond();
    }
}
