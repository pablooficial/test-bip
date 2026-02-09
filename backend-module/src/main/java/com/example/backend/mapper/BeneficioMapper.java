package com.example.backend.mapper;

import com.example.backend.dto.BeneficioCreateDTO;
import com.example.backend.dto.BeneficioResponseDTO;
import com.example.backend.dto.BeneficioUpdateDTO;
import com.example.backend.entity.Beneficio;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre Entity e DTOs
 */
@Component
public class BeneficioMapper {

    /**
     * Converte CreateDTO para Entity
     */
    public Beneficio toEntity(BeneficioCreateDTO dto) {
        Beneficio beneficio = new Beneficio();
        beneficio.setNome(dto.getNome());
        beneficio.setDescricao(dto.getDescricao());
        beneficio.setValor(dto.getValor());
        beneficio.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        return beneficio;
    }

    /**
     * Atualiza Entity com dados do UpdateDTO
     */
    public void updateEntity(Beneficio beneficio, BeneficioUpdateDTO dto) {
        beneficio.setNome(dto.getNome());
        beneficio.setDescricao(dto.getDescricao());
        beneficio.setValor(dto.getValor());
        if (dto.getAtivo() != null) {
            beneficio.setAtivo(dto.getAtivo());
        }
    }

    /**
     * Converte Entity para ResponseDTO
     */
    public BeneficioResponseDTO toResponseDTO(Beneficio beneficio) {
        return new BeneficioResponseDTO(
                beneficio.getId(),
                beneficio.getNome(),
                beneficio.getDescricao(),
                beneficio.getValor(),
                beneficio.getAtivo(),
                beneficio.getVersion()
        );
    }
}
