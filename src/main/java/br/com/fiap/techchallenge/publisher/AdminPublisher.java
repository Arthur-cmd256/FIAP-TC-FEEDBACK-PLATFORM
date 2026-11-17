package br.com.fiap.techchallenge.publisher;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@ApplicationScoped 
public class AdminPublisher {
    
    @Inject 
    SnsClient sns;

    @ConfigProperty(name = "topico.admin.arn", defaultValue = "nao-configurado")
    String topicoAdmin;

    public void enviar(String assunto, String mensagem) {
        sns.publish(PublishRequest.builder()
                .topicArn(topicoAdmin)
                .subject(assunto)
                .message(mensagem)
                .build());
    }
}
