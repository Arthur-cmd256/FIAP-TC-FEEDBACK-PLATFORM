package br.com.fiap.techchallenge.publisher;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.techchallenge.model.Avaliacao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@ApplicationScoped 
public class UrgenciaPublisher {
    
    private static final Logger LOG = Logger.getLogger(UrgenciaPublisher.class);

    @Inject 
    SnsClient snsClient;

    @Inject 
    ObjectMapper mapper;

    @ConfigProperty(name = "topico.urgencia.arn", defaultValue = "nao-configurado")
    String topicoUrgenciaArn;

    public void publicar(Avaliacao avaliacao) {
        try {
            snsClient.publish(
                PublishRequest.builder()
                .topicArn(topicoUrgenciaArn)
                .message(mapper.writeValueAsString(avaliacao))
                .build()
            );
            LOG.infof("Evento de urgencia publicado para avaliacao %s", avaliacao.id());
        } catch (JsonProcessingException e) {
            LOG.error("Erro ao publicar avaliacao", e);
        }
    }
}
