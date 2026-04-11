package br.com.api.petpoints.shared.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class LocalDateTimeUtils {

    public static String converterLocalDateTimeParaPtBr(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        LocalDate data = localDateTime.toLocalDate();
        LocalTime horario = localDateTime.toLocalTime();
        return adicionarZero(data.getDayOfMonth()) + "/" + adicionarZero(data.getMonthValue()) + "/" + adicionarZero(data.getYear()) + " - " + adicionarZero(horario.getHour()) + ":" + adicionarZero(horario.getMinute()) + ":" + adicionarZero(horario.getSecond());
    }

    protected static String adicionarZero(int value) {
        return value < 10 ? "0" + value : String.valueOf(value);
    }
}
