package br.com.fiap.techchallenge.function;

import org.jboss.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.techchallenge.model.Avaliacao;
import br.com.fiap.techchallenge.publisher.AdminPublisher;
import br.com.fiap.techchallenge.util.Datas;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("notificar-urgencia")
public class NotificarUrgenciaHandler implements RequestHandler<SNSEvent, Void> {

    private static final Logger LOG = Logger.getLogger(NotificarUrgenciaHandler.class);

    @Inject 
    ObjectMapper mapper;

    @Inject
    AdminPublisher adminPublisher;

    @Override
    public Void handleRequest(SNSEvent event, Context context) {
        for (SNSEvent.SNSRecord record : event.getRecords()) {
            Avaliacao avaliacao = ler(record.getSNS().getMessage());

            adminPublisher.enviar(
                    "[URGENTE] Avaliacao critica recebida - nota " + avaliacao.nota(),
                    montarCorpo(avaliacao));

            LOG.infof("Aviso de urgencia enviado para avaliacao %s", avaliacao.id());
        }
        return null;
    }

    private Avaliacao ler(String json) {
        try {
            return mapper.readValue(json, Avaliacao.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Mensagem de urgencia invalida: " + json, e);
        }
    }

    private String montarCorpo(Avaliacao a) {
        return "AVISO DE URGENCIA - Nova avaliacao critica\n\n"
                + "Descricao: " + a.descricao() + "\n"
                + "Urgencia: " + a.urgencia() + "\n"
                + "Nota: " + a.nota() + "\n"
                + "Data de envio: " + Datas.formatarDataHora(a.dataEnvio()) + " (horario de Brasilia)\n"
                + "ID: " + a.id() + "\n";
    }
}
