package br.com.fiap.techchallenge.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class UrgenciaTest {
    @Test
    void classificaPorFaixaDeNota() {
        assertEquals(Urgencia.CRITICA, Urgencia.fromNota(0));
        assertEquals(Urgencia.CRITICA, Urgencia.fromNota(3));
        assertEquals(Urgencia.MEDIA, Urgencia.fromNota(4));
        assertEquals(Urgencia.MEDIA, Urgencia.fromNota(6));
        assertEquals(Urgencia.BAIXA, Urgencia.fromNota(7));
        assertEquals(Urgencia.BAIXA, Urgencia.fromNota(10));
    }

    @Test
    void notaForaDaFaixaLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> Urgencia.fromNota(-1));
        assertThrows(IllegalArgumentException.class, () -> Urgencia.fromNota(11));
    }
}
