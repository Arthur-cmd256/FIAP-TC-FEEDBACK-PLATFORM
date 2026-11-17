package br.com.fiap.techchallenge.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AvaliacaoRequest(
        @NotBlank(message = "descricao e obrigatoria")
        @Size(max = 1000, message = "descricao deve ter no maximo 1000 caracteres")
        String descricao,

        @NotNull (message = "nota e obrigatoria")
        @Min(value = 0, message = "nota deve ser no minimo 0")
        @Max(value = 10, message = "nota deve ser no maximo 10")
        Integer nota
) {
    
}
