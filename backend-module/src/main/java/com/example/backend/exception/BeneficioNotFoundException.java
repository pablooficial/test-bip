package com.example.backend.exception;

/**
 * Exceção lançada quando um benefício não é encontrado
 */
public class BeneficioNotFoundException extends RuntimeException {
    
    public BeneficioNotFoundException(String message) {
        super(message);
    }
    
    public BeneficioNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
