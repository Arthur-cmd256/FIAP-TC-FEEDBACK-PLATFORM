package br.com.fiap.techchallenge.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.com.fiap.techchallenge.model.Avaliacao;
import br.com.fiap.techchallenge.model.Urgencia;
import br.com.fiap.techchallenge.service.RelatorioService.Relatorio;

class RelatorioServiceTest {

    private final RelatorioService service = new RelatorioService();
    private final LocalDate inicio = LocalDate.of(2026, 11, 10);
    private final LocalDate fim = LocalDate.of(2026, 11, 16);

    private Avaliacao av(String id, int nota, String dataUtc) {
        return new Avaliacao(id, "descricao " + id, nota, Urgencia.fromNota(nota), dataUtc);
    }

    @Test 
    void calculaMediaEContagens() {
        List<Avaliacao> lista = List.of(
                av("1", 2, "2026-11-10T15:00:00Z"),
                av("2", 8, "2026-11-10T16:00:00Z"),
                av("3", 5, "2026-11-12T15:00:00Z"));

        Relatorio r = service.gerar(lista, inicio, fim);

        assertEquals(3, r.total());
        assertEquals(5.0, r.media(), 0.001);
        assertEquals(7, r.porDia().size());
        assertEquals(2L, r.porDia().get(LocalDate.of(2026, 11, 10)));
        assertEquals(0L, r.porDia().get(LocalDate.of(2026, 11, 11)));
        assertEquals(1L, r.porDia().get(LocalDate.of(2026, 11, 12)));
        assertEquals(1L, r.porUrgencia().get(Urgencia.CRITICA));
        assertEquals(1L, r.porUrgencia().get(Urgencia.MEDIA));
        assertEquals(1L, r.porUrgencia().get(Urgencia.BAIXA));
    }

    @Test
    void relatorioVazioTemMediaZero() {
        Relatorio r = service.gerar(List.of(), inicio, fim);

        assertEquals(0, r.total());
        assertEquals(0.0, r.media(), 0.001);
        assertTrue(service.formatarTexto(r).contains("Nenhuma avaliacao no periodo"));
    }

    @Test
    void textoContemOsDadosExigidos() {
        Relatorio r = service.gerar(List.of(av("1", 2, "2026-11-10T15:00:00Z")), inicio, fim);
        String texto = service.formatarTexto(r);

        assertTrue(texto.contains("Media das notas: 2,00"));
        assertTrue(texto.contains("Avaliacoes por dia"));
        assertTrue(texto.contains("Avaliacoes por urgencia"));
        assertTrue(texto.contains("[CRITICA] 10/11/2026 12:00"));
        assertTrue(texto.contains("descricao 1"));
    }
}