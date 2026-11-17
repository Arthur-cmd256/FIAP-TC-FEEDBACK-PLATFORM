package br.com.fiap.techchallenge.function;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;


import com.amazonaws.services.lambda.runtime.events.SNSEvent;

import br.com.fiap.techchallenge.publisher.AdminPublisher;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest 
class NotificarUrgenciaHandlerTest {

    @Inject 
    NotificarUrgenciaHandler handler;

    @InjectMock 
    AdminPublisher adminPublisher;

    private SNSEvent evento(String mensagem) {
        SNSEvent.SNS sns = new SNSEvent.SNS();
        sns.setMessage(mensagem);
        SNSEvent.SNSRecord record = new SNSEvent.SNSRecord();
        record.setSns(sns);
        SNSEvent event = new SNSEvent();
        event.setRecords(List.of(record));
        return event;
    }

    @Test 
    void enviaEmailComDescricaoUrgenciaEData() {
        String json = "{\"id\":\"abc\",\"descricao\":\"Aula confusa\",\"nota\":2,"
                + "\"urgencia\":\"CRITICA\",\"dataEnvio\":\"2026-11-16T13:45:00Z\"}";

        handler.handleRequest(evento(json), null);

        ArgumentCaptor<String> assunto = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> corpo = ArgumentCaptor.forClass(String.class);
        verify(adminPublisher).enviar(assunto.capture(), corpo.capture());

        assertTrue(assunto.getValue().contains("URGENTE"));
        assertTrue(corpo.getValue().contains("Descricao: Aula confusa"));
        assertTrue(corpo.getValue().contains("Urgencia: CRITICA"));
        assertTrue(corpo.getValue().contains("16/11/2026 10:45"));
    }

    @Test
    void mensagemInvalidaLancaExcecaoParaIrParaDlq() {
        assertThrows(IllegalArgumentException.class,
                () -> handler.handleRequest(evento("isso nao e json"), null));
        verifyNoInteractions(adminPublisher);
    }
}
