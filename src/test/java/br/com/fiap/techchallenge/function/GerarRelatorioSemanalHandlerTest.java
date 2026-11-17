package br.com.fiap.techchallenge.function;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;

import br.com.fiap.techchallenge.publisher.AdminPublisher;
import br.com.fiap.techchallenge.repository.AvaliacaoRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest 
class GerarRelatorioSemanalHandlerTest {

    @Inject 
    GerarRelatorioSemanalHandler handler;

    @InjectMock 
    AvaliacaoRepository repository;

    @InjectMock
    AdminPublisher adminPublisher;

    @Test 
    void enviaRelatorioMesmoSemAvaliacoes() {
        when(repository.buscarDesde(anyString())).thenReturn(List.of());

        handler.handleRequest(new ScheduledEvent(), null);

        ArgumentCaptor<String> assunto = ArgumentCaptor.forClass(String.class);
        verify(adminPublisher).enviar(assunto.capture(), anyString());
        assertTrue(assunto.getValue().startsWith("Relatorio semanal"));
    }
}