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
 * DTO para atualização de Benefício
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para atualização de um benefício")
public class BeneficioUpdateDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Schema(description = "Nome do benefício", example = "Benefício A Atualizado", required = true)
    private String nome;

    @Schema(description = "Descrição do benefício", example = "Nova descrição")
    private String descricao;

    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    @Schema(description = "Valor do benefício", example = "1500.00", required = true)
    private BigDecimal valor;

    @Schema(description = "Indica se o benefício está ativo", example = "true")
    private Boolean ativo;
}
