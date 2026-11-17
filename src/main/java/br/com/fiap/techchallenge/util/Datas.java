package br.com.fiap.techchallenge.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class Datas {

    public static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Datas() {
    }

    public static String formatarDataHora(String isoUtc) {
        return Instant.parse(isoUtc).atZone(ZONA).format(DATA_HORA);
    }

    public static String formatarData(LocalDate data) {
        return data.format(DATA);
    }

    public static LocalDate dia(String isoUtc) {
        return Instant.parse(isoUtc).atZone(ZONA).toLocalDate();
    }
}
