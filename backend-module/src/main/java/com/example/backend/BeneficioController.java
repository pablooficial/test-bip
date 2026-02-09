package com.example.backend;

import com.example.backend.dto.BeneficioCreateDTO;
import com.example.backend.dto.BeneficioResponseDTO;
import com.example.backend.dto.BeneficioUpdateDTO;
import com.example.backend.dto.TransferenciaDTO;
import com.example.backend.exception.ErrorResponse;
import com.example.backend.service.BeneficioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para operações com Benefícios
 */
@RestController
@RequestMapping("/api/v1/beneficios")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Benefícios", description = "API para gerenciamento de benefícios")
@CrossOrigin(origins = "*") // Para desenvolvimento - em produção, configurar CORS adequadamente
public class BeneficioController {

    private final BeneficioService service;

    @Operation(summary = "Listar todos os benefícios", description = "Retorna lista completa de benefícios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BeneficioResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<BeneficioResponseDTO>> findAll() {
        log.info("GET /api/v1/beneficios - Listando todos os benefícios");
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Buscar benefício por ID", description = "Retorna um benefício específico pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Benefício encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BeneficioResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Benefício não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<BeneficioResponseDTO> findById(
            @Parameter(description = "ID do benefício", required = true)
            @PathVariable Long id) {
        log.info("GET /api/v1/beneficios/{} - Buscando benefício", id);
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Listar benefícios ativos", description = "Retorna apenas benefícios ativos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BeneficioResponseDTO.class)))
    })
    @GetMapping("/ativos")
    public ResponseEntity<List<BeneficioResponseDTO>> findAtivos() {
        log.info("GET /api/v1/beneficios/ativos - Listando benefícios ativos");
        return ResponseEntity.ok(service.findAtivos());
    }

    @Operation(summary = "Buscar benefícios por nome", description = "Busca benefícios que contenham o nome especificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BeneficioResponseDTO.class)))
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<BeneficioResponseDTO>> findByNome(
            @Parameter(description = "Nome ou parte do nome para buscar", required = true)
            @RequestParam String nome) {
        log.info("GET /api/v1/beneficios/buscar?nome={}", nome);
        return ResponseEntity.ok(service.findByNome(nome));
    }

    @Operation(summary = "Criar novo benefício", description = "Cria um novo benefício no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Benefício criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BeneficioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<BeneficioResponseDTO> create(
            @Parameter(description = "Dados do benefício a ser criado", required = true)
            @Valid @RequestBody BeneficioCreateDTO dto) {
        log.info("POST /api/v1/beneficios - Criando benefício: {}", dto.getNome());
        BeneficioResponseDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Atualizar benefício", description = "Atualiza um benefício existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Benefício atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BeneficioResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Benefício não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<BeneficioResponseDTO> update(
            @Parameter(description = "ID do benefício", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novos dados do benefício", required = true)
            @Valid @RequestBody BeneficioUpdateDTO dto) {
        log.info("PUT /api/v1/beneficios/{} - Atualizando benefício", id);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Remover benefício", description = "Remove um benefício (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Benefício removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Benefício não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do benefício", required = true)
            @PathVariable Long id) {
        log.info("DELETE /api/v1/beneficios/{} - Removendo benefício", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Transferir valor entre benefícios",
            description = "Realiza transferência de valor de um benefício para outro com validações")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou saldo insuficiente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Benefício não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/transferir")
    public ResponseEntity<Void> transfer(
            @Parameter(description = "Dados da transferência", required = true)
            @Valid @RequestBody TransferenciaDTO dto) {
        log.info("POST /api/v1/beneficios/transferir - Transferindo {} de {} para {}",
                dto.getAmount(), dto.getFromId(), dto.getToId());
        service.transfer(dto);
        return ResponseEntity.ok().build();
    }
}

