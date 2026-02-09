package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para requisição de transferência entre benefícios
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para transferência entre benefícios")
public class TransferenciaDTO {

    @NotNull(message = "ID de origem é obrigatório")
    @Schema(description = "ID do benefício de origem", example = "1", required = true)
    private Long fromId;

    @NotNull(message = "ID de destino é obrigatório")
    @Schema(description = "ID do benefício de destino", example = "2", required = true)
    private Long toId;

    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    @Schema(description = "Valor a ser transferido", example = "300.00", required = true)
    private BigDecimal amount;
}
