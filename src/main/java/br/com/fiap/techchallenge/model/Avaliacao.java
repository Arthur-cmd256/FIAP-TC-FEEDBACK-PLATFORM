package br.com.fiap.techchallenge.model;

public record Avaliacao(
    String id, String descricao, int nota, Urgencia urgencia, String dataEnvio
) {
    
}
