package com.example.backend.service;

import com.example.backend.dto.BeneficioCreateDTO;
import com.example.backend.dto.BeneficioResponseDTO;
import com.example.backend.dto.BeneficioUpdateDTO;
import com.example.backend.dto.TransferenciaDTO;
import com.example.backend.entity.Beneficio;
import com.example.backend.exception.BeneficioNotFoundException;
import com.example.backend.mapper.BeneficioMapper;
import com.example.backend.repository.BeneficioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para operações de negócio com Benefícios
 * Integra com o módulo EJB quando necessário
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BeneficioService {

    private final BeneficioRepository repository;
    private final BeneficioMapper mapper;
    // Nota: Em produção, injetar o EJB via JNDI lookup ou Spring Integration
    // private final BeneficioEjbService ejbService;

    /**
     * Lista todos os benefícios
     */
    @Transactional(readOnly = true)
    public List<BeneficioResponseDTO> findAll() {
        log.info("Buscando todos os benefícios");
        return repository.findAll().stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca benefício por ID
     */
    @Transactional(readOnly = true)
    public BeneficioResponseDTO findById(Long id) {
        log.info("Buscando benefício com ID: {}", id);
        Beneficio beneficio = repository.findById(id)
                .orElseThrow(() -> new BeneficioNotFoundException("Benefício não encontrado: " + id));
        return mapper.toResponseDTO(beneficio);
    }

    /**
     * Busca benefícios ativos
     */
    @Transactional(readOnly = true)
    public List<BeneficioResponseDTO> findAtivos() {
        log.info("Buscando benefícios ativos");
        return repository.findByAtivoTrue().stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca benefícios por nome
     */
    @Transactional(readOnly = true)
    public List<BeneficioResponseDTO> findByNome(String nome) {
        log.info("Buscando benefícios com nome contendo: {}", nome);
        return repository.findByNomeContainingIgnoreCase(nome).stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Cria novo benefício
     */
    @Transactional
    public BeneficioResponseDTO create(BeneficioCreateDTO dto) {
        log.info("Criando novo benefício: {}", dto.getNome());
        Beneficio beneficio = mapper.toEntity(dto);
        Beneficio saved = repository.save(beneficio);
        log.info("Benefício criado com ID: {}", saved.getId());
        return mapper.toResponseDTO(saved);
    }

    /**
     * Atualiza benefício existente
     */
    @Transactional
    public BeneficioResponseDTO update(Long id, BeneficioUpdateDTO dto) {
        log.info("Atualizando benefício ID: {}", id);
        Beneficio beneficio = repository.findById(id)
                .orElseThrow(() -> new BeneficioNotFoundException("Benefício não encontrado: " + id));
        
        mapper.updateEntity(beneficio, dto);
        Beneficio updated = repository.save(beneficio);
        log.info("Benefício atualizado: {}", id);
        return mapper.toResponseDTO(updated);
    }

    /**
     * Remove benefício (soft delete)
     */
    @Transactional
    public void delete(Long id) {
        log.info("Removendo benefício ID: {}", id);
        Beneficio beneficio = repository.findById(id)
                .orElseThrow(() -> new BeneficioNotFoundException("Benefício não encontrado: " + id));
        
        beneficio.setAtivo(false);
        repository.save(beneficio);
        log.info("Benefício removido (soft delete): {}", id);
    }

    /**
     * Realiza transferência entre benefícios
     * Implementação direta no Spring Boot (sem EJB para simplificar)
     * Em produção, delegaria para o EJB Service
     */
    @Transactional
    public void transfer(TransferenciaDTO dto) {
        log.info("Iniciando transferência: {} -> {}, valor: {}", 
                dto.getFromId(), dto.getToId(), dto.getAmount());

        // Validações
        if (dto.getFromId().equals(dto.getToId())) {
            throw new IllegalArgumentException("Não é possível transferir para o mesmo benefício");
        }

        // Buscar benefícios com lock pessimista
        Beneficio from = repository.findById(dto.getFromId())
                .orElseThrow(() -> new BeneficioNotFoundException("Benefício de origem não encontrado: " + dto.getFromId()));
        
        Beneficio to = repository.findById(dto.getToId())
                .orElseThrow(() -> new BeneficioNotFoundException("Benefício de destino não encontrado: " + dto.getToId()));

        // Validar se estão ativos
        if (!from.getAtivo()) {
            throw new IllegalArgumentException("Benefício de origem está inativo");
        }
        if (!to.getAtivo()) {
            throw new IllegalArgumentException("Benefício de destino está inativo");
        }

        // Validar saldo
        if (from.getValor().compareTo(dto.getAmount()) < 0) {
            throw new IllegalArgumentException(
                    String.format("Saldo insuficiente. Disponível: %s, Solicitado: %s",
                            from.getValor(), dto.getAmount()));
        }

        // Realizar transferência
        from.setValor(from.getValor().subtract(dto.getAmount()));
        to.setValor(to.getValor().add(dto.getAmount()));

        repository.save(from);
        repository.save(to);

        log.info("Transferência concluída com sucesso");
    }
}
