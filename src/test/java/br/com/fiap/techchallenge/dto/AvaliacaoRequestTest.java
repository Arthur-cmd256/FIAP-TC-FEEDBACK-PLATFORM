package br.com.fiap.techchallenge.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.validation.Validator;

@QuarkusTest 
public class AvaliacaoRequestTest {
    @Inject 
    Validator validator;

    @Test
    void requestValidoNaoTemViolacoes() {
        assertTrue(validator.validate(new AvaliacaoRequest("Aula boa", 8)).isEmpty());
    }

    @Test
    void descricaoVaziaEInvalida() {
        assertEquals(1, validator.validate(new AvaliacaoRequest("  ", 5)).size());
    }

    @Test
    void notaForaDaFaixaEInvalida() {
        assertEquals(1, validator.validate(new AvaliacaoRequest("Aula", 11)).size());
        assertEquals(1, validator.validate(new AvaliacaoRequest("Aula", -1)).size());
    }

    @Test
    void notaAusenteEInvalida() {
        assertEquals(1, validator.validate(new AvaliacaoRequest("Aula", null)).size());
    }
}
