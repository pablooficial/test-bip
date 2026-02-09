package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para criação de Benefício
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação de um benefício")
public class BeneficioCreateDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Schema(description = "Nome do benefício", example = "Benefício A", required = true)
    private String nome;

    @Schema(description = "Descrição do benefício", example = "Descrição detalhada do benefício")
    private String descricao;

    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    @Schema(description = "Valor do benefício", example = "1000.00", required = true)
    private BigDecimal valor;

    @Schema(description = "Indica se o benefício está ativo", example = "true", defaultValue = "true")
    private Boolean ativo = true;
}
