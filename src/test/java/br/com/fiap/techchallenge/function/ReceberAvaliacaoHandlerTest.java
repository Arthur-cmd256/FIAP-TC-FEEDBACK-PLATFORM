package br.com.fiap.techchallenge.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest 
class ReceberAvaliacaoHandlerTest {

    @Inject 
    ReceberAvaliacaoHandler handler;

    private APIGatewayProxyResponseEvent chamar(String body) {
        return handler.handleRequest(new APIGatewayProxyRequestEvent().withBody(body), null);
    }

    @Test 
    void notaBaixaRetorna201ComUrgenciaCritica() {
        var resp = chamar("{\"descricao\":\"Aula confusa\",\"nota\":2}");
        assertEquals(201, resp.getStatusCode());
        assertTrue(resp.getBody().contains("CRITICA"));
    }

    @Test
    void notaAltaRetorna201ComUrgenciaBaixa() {
        var resp = chamar("{\"descricao\":\"Gostei\",\"nota\":9}");
        assertEquals(201, resp.getStatusCode());
        assertTrue(resp.getBody().contains("BAIXA"));
    }

    @Test
    void notaForaDaFaixaRetorna400() {
        assertEquals(400, chamar("{\"descricao\":\"Aula\",\"nota\":11}").getStatusCode());
    }

    @Test
    void descricaoVaziaRetorna400() {
        assertEquals(400, chamar("{\"descricao\":\"\",\"nota\":5}").getStatusCode());
    }

    @Test
    void jsonInvalidoRetorna400() {
        assertEquals(400, chamar("isso nao e json").getStatusCode());
    }

    @Test
    void corpoAusenteRetorna400() {
        assertEquals(400, chamar(null).getStatusCode());
    }

}
