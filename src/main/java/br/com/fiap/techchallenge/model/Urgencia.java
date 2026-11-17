package br.com.fiap.techchallenge.model;

public enum Urgencia {
    CRITICA,
    MEDIA,
    BAIXA;

    public static Urgencia fromNota(int nota) {
        if (nota < 0 || nota > 10) {
            throw new IllegalArgumentException("Nota deve estar entre 0 e 10");
        }
        
        if ( nota <= 3) return CRITICA;
        if (nota <= 6) return MEDIA;
        return BAIXA;

    }
}
