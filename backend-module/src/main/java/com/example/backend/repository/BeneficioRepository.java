package com.example.backend.repository;

import com.example.backend.entity.Beneficio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operações de banco de dados com Beneficio
 */
@Repository
public interface BeneficioRepository extends JpaRepository<Beneficio, Long> {

    /**
     * Busca todos os benefícios ativos
     */
    List<Beneficio> findByAtivoTrue();

    /**
     * Busca benefícios por nome (case insensitive)
     */
    @Query("SELECT b FROM Beneficio b WHERE LOWER(b.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Beneficio> findByNomeContainingIgnoreCase(String nome);
}
