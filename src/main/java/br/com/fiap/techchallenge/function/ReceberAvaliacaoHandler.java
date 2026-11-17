package br.com.fiap.techchallenge.function;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.jboss.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.techchallenge.dto.AvaliacaoRequest;
import br.com.fiap.techchallenge.model.Avaliacao;
import br.com.fiap.techchallenge.model.Urgencia;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import com.fasterxml.jackson.core.JsonProcessingException;

@Named("receber-avaliacao")
public class ReceberAvaliacaoHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger LOG = Logger.getLogger(ReceberAvaliacaoHandler.class);

    @Inject 
    ObjectMapper mapper;

    @Inject
    Validator validator;

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        String body = event.getBody();
        if (body == null || body.isBlank()) {
            return resposta(400, Map.of("erro", "Corpo da requisicao e obrigatorio"));
        }

        AvaliacaoRequest request;
        try {
            request = mapper.readValue(body, AvaliacaoRequest.class);
        } catch (JsonProcessingException e) {
            return resposta(400, Map.of("erro", "JSON invalido: esperado {\"descricao\": string, \"nota\": 0-10}"));
        }

        Set<ConstraintViolation<AvaliacaoRequest>> violacoes = validator.validate(request);
        if (!violacoes.isEmpty()) {
            List<String> erros = violacoes.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .sorted()
                    .toList();
            return resposta(400, Map.of("erros", erros));
        }

        Urgencia urgencia = Urgencia.fromNota(request.nota());
        Avaliacao avaliacao = new Avaliacao(
                UUID.randomUUID().toString(),
                request.descricao().trim(),
                request.nota(),
                urgencia,
                Instant.now().toString());

        LOG.infof("Avaliacao %s recebida (nota=%d, urgencia=%s)", avaliacao.id(), avaliacao.nota(), urgencia);

        return resposta(201, Map.of(
                "id", avaliacao.id(),
                "urgencia", urgencia.name(),
                "dataEnvio", avaliacao.dataEnvio()));
    }

    private APIGatewayProxyResponseEvent resposta(int status, Object corpo) {
        try {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(status)
                    .withHeaders(Map.of("Content-Type", "application/json"))
                    .withBody(mapper.writeValueAsString(corpo));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
    
}
