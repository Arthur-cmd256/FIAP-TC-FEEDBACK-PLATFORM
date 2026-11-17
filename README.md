# feedback-platform

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/feedback-platform-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- AWS Lambda ([guide](https://quarkus.io/guides/aws-lambda)): Write AWS Lambda functions
- Hibernate Validator ([guide](https://quarkus.io/guides/validation)): Bean validation using Hibernate Validator and Jakarta Validation annotations
- Logging JSON ([guide](https://quarkus.io/guides/logging#json-logging)): Add JSON formatter for console logging
- Amazon DynamoDB ([guide](https://docs.quarkiverse.io/quarkus-amazon-services/dev/amazon-dynamodb.html)): Connect to Amazon DynamoDB datastore
- Amazon SNS ([guide](https://docs.quarkiverse.io/quarkus-amazon-services/dev/amazon-sns.html)): Connect to Amazon SNS pub/sub messaging service

## Provided Code

### Amazon Lambda Integration example

This example contains a Quarkus Greeting Lambda ready for Amazon.

[Related guide section...](https://quarkus.io/guides/aws-lambda)


# FIAP-TC-FEEDBACK-PLATFORM

## Limitação conhecida

Para avaliações críticas, a aplicação grava a avaliação no DynamoDB antes de publicar o aviso no SNS. Essa ordem evita que um aviso seja enviado para uma avaliação que não foi armazenada. Porém, se a publicação no SNS falhar, a exceção é propagada e a API responde com erro, mesmo que a avaliação já esteja gravada no banco. Para este projeto, esse comportamento é aceitável e o erro dispara o alarme configurado.

## Consulta de avaliações

A busca de avaliações a partir de uma data usa `Scan` com filtro no DynamoDB. Para o volume atual deste projeto, o custo e o desempenho dessa abordagem são aceitáveis. O filtro é aplicado após a leitura dos itens, portanto, se o volume crescer, uma melhoria futura é adicionar um GSI por dia e consultar esse índice com `Query`, evitando varrer a tabela inteira.

## Autenticação do pipeline de deploy

O ideal é autenticar o GitHub Actions na AWS por OIDC, assumindo uma role IAM com privilégios mínimos e restrita ao repositório e ao contexto de deploy. A SCP da organização impediu a criação do provedor OIDC necessário na conta AWS. Por isso, como alternativa temporária, o pipeline usa as credenciais `AWS_ACCESS_KEY_ID` e `AWS_SECRET_ACCESS_KEY` armazenadas nos secrets do GitHub. Migrar para OIDC é uma melhoria futura.
