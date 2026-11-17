package br.com.fiap.techchallenge.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import br.com.fiap.techchallenge.model.Avaliacao;
import br.com.fiap.techchallenge.model.Urgencia;
import br.com.fiap.techchallenge.util.Datas;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped 
public class RelatorioService {

    private static final int LIMITE_LISTAGEM = 100;
    private static final int LIMITE_DESCRICAO = 200;
    private static final Locale PT_BR = Locale.of("pt", "BR");

    public record Relatorio(LocalDate inicio, LocalDate fim, int total, double media,
                            Map<LocalDate, Long> porDia, Map<Urgencia, Long> porUrgencia,
                            List<Avaliacao> avaliacoes) {
    }

    public Relatorio gerar(List<Avaliacao> avaliacoes, LocalDate inicio, LocalDate fim) {
        List<Avaliacao> ordenadas = avaliacoes.stream()
                .sorted(Comparator.comparing(Avaliacao::dataEnvio))
                .toList();

        Map<LocalDate, Long> porDia = new TreeMap<>();
        for (LocalDate d = inicio; !d.isAfter(fim); d = d.plusDays(1)) {
            porDia.put(d, 0L);
        }
        Map<Urgencia, Long> porUrgencia = new EnumMap<>(Urgencia.class);
        for (Urgencia u : Urgencia.values()) {
            porUrgencia.put(u, 0L);
        }
        for (Avaliacao a : ordenadas) {
            porDia.merge(Datas.dia(a.dataEnvio()), 1L, Long::sum);
            porUrgencia.merge(a.urgencia(), 1L, Long::sum);
        }

        double media = ordenadas.stream().mapToInt(Avaliacao::nota).average().orElse(0.0);
        return new Relatorio(inicio, fim, ordenadas.size(), media, porDia, porUrgencia, ordenadas);
    }

    public String assunto(Relatorio r) {
        return "Relatorio semanal de feedbacks - " + Datas.formatarData(r.inicio())
                + " a " + Datas.formatarData(r.fim());
    }

    public String formatarTexto(Relatorio r) {
        StringBuilder sb = new StringBuilder();
        sb.append("RELATORIO SEMANAL DE FEEDBACKS\n");
        sb.append("Periodo: ").append(Datas.formatarData(r.inicio()))
                .append(" a ").append(Datas.formatarData(r.fim())).append("\n\n");
        sb.append("Total de avaliacoes: ").append(r.total()).append("\n");
        sb.append(String.format(PT_BR, "Media das notas: %.2f%n", r.media()));

        sb.append("\nAvaliacoes por dia:\n");
        r.porDia().forEach((dia, qtd) ->
                sb.append("  ").append(Datas.formatarData(dia)).append(": ").append(qtd).append("\n"));

        sb.append("\nAvaliacoes por urgencia:\n");
        r.porUrgencia().forEach((urgencia, qtd) ->
                sb.append("  ").append(urgencia).append(": ").append(qtd).append("\n"));

        sb.append("\nAvaliacoes do periodo (descricao, urgencia, data de envio):\n");
        if (r.avaliacoes().isEmpty()) {
            sb.append("  Nenhuma avaliacao no periodo.\n");
        }
        int exibidas = Math.min(LIMITE_LISTAGEM, r.avaliacoes().size());
        for (Avaliacao a : r.avaliacoes().subList(0, exibidas)) {
            sb.append("  [").append(a.urgencia()).append("] ")
                    .append(Datas.formatarDataHora(a.dataEnvio()))
                    .append(" - nota ").append(a.nota())
                    .append(" - ").append(resumir(a.descricao())).append("\n");
        }
        if (r.avaliacoes().size() > exibidas) {
            sb.append("  ... e mais ").append(r.avaliacoes().size() - exibidas).append(" avaliacoes.\n");
        }
        return sb.toString();
    }

    private static String resumir(String texto) {
        String limpo = texto.replaceAll("\\s+", " ").trim();
        return limpo.length() > LIMITE_DESCRICAO ? limpo.substring(0, LIMITE_DESCRICAO) + "..." : limpo;
    }
}
