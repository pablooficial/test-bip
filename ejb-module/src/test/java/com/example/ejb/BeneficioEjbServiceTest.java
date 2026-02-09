package com.example.ejb;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para BeneficioEjbService
 * Testa todas as validações, locking e cenários de erro
 */
@ExtendWith(MockitoExtension.class)
class BeneficioEjbServiceTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private BeneficioEjbService service;

    private Beneficio beneficioA;
    private Beneficio beneficioB;

    @BeforeEach
    void setUp() {
        beneficioA = new Beneficio("Beneficio A", "Descrição A", new BigDecimal("1000.00"), true);
        beneficioA.setId(1L);
        beneficioA.setVersion(0L);

        beneficioB = new Beneficio("Beneficio B", "Descrição B", new BigDecimal("500.00"), true);
        beneficioB.setId(2L);
        beneficioB.setVersion(0L);
    }

    @Test
    void testTransfer_Success() {
        // Arrange
        BigDecimal transferAmount = new BigDecimal("300.00");
        when(em.find(eq(Beneficio.class), eq(1L), eq(LockModeType.PESSIMISTIC_WRITE)))
                .thenReturn(beneficioA);
        when(em.find(eq(Beneficio.class), eq(2L), eq(LockModeType.PESSIMISTIC_WRITE)))
                .thenReturn(beneficioB);

        // Act
        service.transfer(1L, 2L, transferAmount);

        // Assert
        assertEquals(new BigDecimal("700.00"), beneficioA.getValor());
        assertEquals(new BigDecimal("800.00"), beneficioB.getValor());
        verify(em).merge(beneficioA);
        verify(em).merge(beneficioB);
    }

    @Test
    void testTransfer_InsufficientBalance() {
        // Arrange
        BigDecimal transferAmount = new BigDecimal("1500.00"); // Mais que o saldo
        when(em.find(eq(Beneficio.class), eq(1L), eq(LockModeType.PESSIMISTIC_WRITE)))
                .thenReturn(beneficioA);
        when(em.find(eq(Beneficio.class), eq(2L), eq(LockModeType.PESSIMISTIC_WRITE)))
                .thenReturn(beneficioB);

        // Act & Assert
        BeneficioEjbService.InsufficientBalanceException exception = 
            assertThrows(BeneficioEjbService.InsufficientBalanceException.class, 
                () -> service.transfer(1L, 2L, transferAmount));
        
        assertTrue(exception.getMessage().contains("Saldo insuficiente"));
        verify(em, never()).merge(any());
    }

    @Test
    void testTransfer_BeneficioNotFound() {
        // Arrange
        when(em.find(eq(Beneficio.class), eq(1L), eq(LockModeType.PESSIMISTIC_WRITE)))
                .thenReturn(null);

        // Act & Assert
        assertThrows(BeneficioEjbService.BeneficioNotFoundException.class,
                () -> service.transfer(1L, 2L, new BigDecimal("100.00")));
    }

    @Test
    void testTransfer_InactiveBeneficio() {
        // Arrange
        beneficioA.setAtivo(false);
        when(em.find(eq(Beneficio.class), eq(1L), eq(LockModeType.PESSIMISTIC_WRITE)))
                .thenReturn(beneficioA);
        when(em.find(eq(Beneficio.class), eq(2L), eq(LockModeType.PESSIMISTIC_WRITE)))
                .thenReturn(beneficioB);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.transfer(1L, 2L, new BigDecimal("100.00")));
        
        assertTrue(exception.getMessage().contains("inativo"));
    }

    @Test
    void testTransfer_NullParameters() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(null, 2L, new BigDecimal("100.00")));
        
        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(1L, null, new BigDecimal("100.00")));
        
        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(1L, 2L, null));
    }

    @Test
    void testTransfer_NegativeAmount() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(1L, 2L, new BigDecimal("-100.00")));
    }

    @Test
    void testTransfer_ZeroAmount() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(1L, 2L, BigDecimal.ZERO));
    }

    @Test
    void testTransfer_SameId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(1L, 1L, new BigDecimal("100.00")));
    }

    @Test
    void testFindAll() {
        // Arrange
        List<Beneficio> beneficios = Arrays.asList(beneficioA, beneficioB);
        when(em.createQuery(anyString(), eq(Beneficio.class)))
                .thenReturn(mock(jakarta.persistence.TypedQuery.class));
        when(em.createQuery(anyString(), eq(Beneficio.class)).getResultList())
                .thenReturn(beneficios);

        // Act
        List<Beneficio> result = service.findAll();

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void testFindById_Success() {
        // Arrange
        when(em.find(Beneficio.class, 1L)).thenReturn(beneficioA);

        // Act
        Beneficio result = service.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        when(em.find(Beneficio.class, 999L)).thenReturn(null);

        // Act & Assert
        assertThrows(BeneficioEjbService.BeneficioNotFoundException.class,
                () -> service.findById(999L));
    }

    @Test
    void testCreate_Success() {
        // Arrange
        Beneficio newBeneficio = new Beneficio("Novo", "Desc", new BigDecimal("100.00"), true);

        // Act
        service.create(newBeneficio);

        // Assert
        verify(em).persist(newBeneficio);
        verify(em).flush();
    }

    @Test
    void testCreate_WithId() {
        // Arrange
        Beneficio newBeneficio = new Beneficio("Novo", "Desc", new BigDecimal("100.00"), true);
        newBeneficio.setId(1L);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> service.create(newBeneficio));
    }

    @Test
    void testUpdate_Success() {
        // Arrange
        when(em.find(Beneficio.class, 1L)).thenReturn(beneficioA);
        when(em.merge(beneficioA)).thenReturn(beneficioA);

        // Act
        Beneficio result = service.update(beneficioA);

        // Assert
        assertNotNull(result);
        verify(em).merge(beneficioA);
    }

    @Test
    void testDelete_SoftDelete() {
        // Arrange
        when(em.find(Beneficio.class, 1L)).thenReturn(beneficioA);

        // Act
        service.delete(1L);

        // Assert
        assertFalse(beneficioA.getAtivo());
        verify(em).merge(beneficioA);
    }
}
