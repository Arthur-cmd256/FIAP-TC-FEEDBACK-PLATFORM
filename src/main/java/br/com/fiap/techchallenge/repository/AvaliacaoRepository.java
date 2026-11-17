package br.com.fiap.techchallenge.repository;

import java.util.Map;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import br.com.fiap.techchallenge.model.Avaliacao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

@ApplicationScoped 
public class AvaliacaoRepository {
    
    @Inject
    DynamoDbClient dynamoDbClient;

    @ConfigProperty(name = "tabela.avaliacoes", defaultValue = "nao-configurado")
    String tabela;

    public void salvar(Avaliacao a){
        dynamoDbClient.putItem(
            PutItemRequest.builder()
            .tableName(tabela)
            .item(Map.of(
                "id", s(a.id()),
                "descricao", s(a.descricao()),
                "nota", n(a.nota()),
                "urgencia", s(a.urgencia().name()),
                "dataEnvio", s(a.dataEnvio())))
            .build());
    }

    private static AttributeValue s(String valor) {
        return AttributeValue.builder().s(valor).build();
    }

    private static AttributeValue n(int valor) {
        return AttributeValue.builder().n(String.valueOf(valor)).build();
    }
}
