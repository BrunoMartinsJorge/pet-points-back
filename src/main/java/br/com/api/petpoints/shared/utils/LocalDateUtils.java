package br.com.api.petpoints.shared.utils;

import java.time.LocalDate;

public class LocalDateUtils {

    public static String converterLocalDateParaPtBr(LocalDate localDate) {
        if (localDate == null) return null;
        return localDate.getDayOfMonth() + "/" + localDate.getMonthValue() + "/" + localDate.getYear();
    }
}
