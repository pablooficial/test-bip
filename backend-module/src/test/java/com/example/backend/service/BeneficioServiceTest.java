package com.example.backend.service;

import com.example.backend.dto.BeneficioCreateDTO;
import com.example.backend.dto.BeneficioResponseDTO;
import com.example.backend.dto.BeneficioUpdateDTO;
import com.example.backend.dto.TransferenciaDTO;
import com.example.backend.entity.Beneficio;
import com.example.backend.exception.BeneficioNotFoundException;
import com.example.backend.mapper.BeneficioMapper;
import com.example.backend.repository.BeneficioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeneficioServiceTest {

    @Mock
    private BeneficioRepository repository;

    @Mock
    private BeneficioMapper mapper;

    @InjectMocks
    private BeneficioService service;

    private Beneficio beneficio;
    private BeneficioResponseDTO responseDTO;
    private BeneficioCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        beneficio = new Beneficio(1L, "Beneficio A", "Descrição A", 
                new BigDecimal("1000.00"), true, 0L);
        
        responseDTO = new BeneficioResponseDTO(1L, "Beneficio A", "Descrição A",
                new BigDecimal("1000.00"), true, 0L);
        
        createDTO = new BeneficioCreateDTO("Beneficio A", "Descrição A",
                new BigDecimal("1000.00"), true);
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(Arrays.asList(beneficio));
        when(mapper.toResponseDTO(any())).thenReturn(responseDTO);

        List<BeneficioResponseDTO> result = service.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).findAll();
    }

    @Test
    void testFindById_Success() {
        when(repository.findById(1L)).thenReturn(Optional.of(beneficio));
        when(mapper.toResponseDTO(beneficio)).thenReturn(responseDTO);

        BeneficioResponseDTO result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(repository).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BeneficioNotFoundException.class, () -> service.findById(999L));
    }

    @Test
    void testCreate() {
        when(mapper.toEntity(createDTO)).thenReturn(beneficio);
        when(repository.save(beneficio)).thenReturn(beneficio);
        when(mapper.toResponseDTO(beneficio)).thenReturn(responseDTO);

        BeneficioResponseDTO result = service.create(createDTO);

        assertNotNull(result);
        verify(repository).save(beneficio);
    }

    @Test
    void testUpdate_Success() {
        BeneficioUpdateDTO updateDTO = new BeneficioUpdateDTO("Beneficio A Updated",
                "Nova descrição", new BigDecimal("1500.00"), true);
        
        when(repository.findById(1L)).thenReturn(Optional.of(beneficio));
        when(repository.save(beneficio)).thenReturn(beneficio);
        when(mapper.toResponseDTO(beneficio)).thenReturn(responseDTO);

        BeneficioResponseDTO result = service.update(1L, updateDTO);

        assertNotNull(result);
        verify(repository).save(beneficio);
    }

    @Test
    void testDelete() {
        when(repository.findById(1L)).thenReturn(Optional.of(beneficio));

        service.delete(1L);

        assertFalse(beneficio.getAtivo());
        verify(repository).save(beneficio);
    }

    @Test
    void testTransfer_Success() {
        Beneficio from = new Beneficio(1L, "From", "Desc", new BigDecimal("1000.00"), true, 0L);
        Beneficio to = new Beneficio(2L, "To", "Desc", new BigDecimal("500.00"), true, 0L);
        TransferenciaDTO dto = new TransferenciaDTO(1L, 2L, new BigDecimal("300.00"));

        when(repository.findById(1L)).thenReturn(Optional.of(from));
        when(repository.findById(2L)).thenReturn(Optional.of(to));

        service.transfer(dto);

        assertEquals(new BigDecimal("700.00"), from.getValor());
        assertEquals(new BigDecimal("800.00"), to.getValor());
        verify(repository, times(2)).save(any());
    }

    @Test
    void testTransfer_InsufficientBalance() {
        Beneficio from = new Beneficio(1L, "From", "Desc", new BigDecimal("100.00"), true, 0L);
        Beneficio to = new Beneficio(2L, "To", "Desc", new BigDecimal("500.00"), true, 0L);
        TransferenciaDTO dto = new TransferenciaDTO(1L, 2L, new BigDecimal("300.00"));

        when(repository.findById(1L)).thenReturn(Optional.of(from));
        when(repository.findById(2L)).thenReturn(Optional.of(to));

        assertThrows(IllegalArgumentException.class, () -> service.transfer(dto));
    }

    @Test
    void testTransfer_SameId() {
        TransferenciaDTO dto = new TransferenciaDTO(1L, 1L, new BigDecimal("300.00"));

        assertThrows(IllegalArgumentException.class, () -> service.transfer(dto));
    }
}
