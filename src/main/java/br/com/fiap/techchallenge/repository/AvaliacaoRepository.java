package br.com.fiap.techchallenge.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import br.com.fiap.techchallenge.model.Avaliacao;
import br.com.fiap.techchallenge.model.Urgencia;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

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

    public List<Avaliacao> buscarDesde(String inicioIso) {
        ScanRequest request = ScanRequest.builder()
                .tableName(tabela)
                .filterExpression("dataEnvio >= :inicio")
                .expressionAttributeValues(Map.of(":inicio", s(inicioIso)))
                .build();

        List<Avaliacao> resultado = new ArrayList<>();
        dynamoDbClient.scanPaginator(request).forEach(pagina ->
                pagina.items().forEach(item -> resultado.add(new Avaliacao(
                        item.get("id").s(),
                        item.get("descricao").s(),
                        Integer.parseInt(item.get("nota").n()),
                        Urgencia.valueOf(item.get("urgencia").s()),
                        item.get("dataEnvio").s()))));
        return resultado;
    }
}
