package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de resposta para Benefício
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de um benefício")
public class BeneficioResponseDTO {

    @Schema(description = "ID do benefício", example = "1")
    private Long id;

    @Schema(description = "Nome do benefício", example = "Benefício A")
    private String nome;

    @Schema(description = "Descrição do benefício", example = "Descrição detalhada")
    private String descricao;

    @Schema(description = "Valor do benefício", example = "1000.00")
    private BigDecimal valor;

    @Schema(description = "Indica se o benefício está ativo", example = "true")
    private Boolean ativo;

    @Schema(description = "Versão para controle de concorrência", example = "0")
    private Long version;
}
