package com.example.ejb;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;

/**
 * Serviço EJB para operações de Benefício
 * Implementa transações com PESSIMISTIC LOCKING para evitar race conditions
 */
@Stateless
public class BeneficioEjbService {

    @PersistenceContext
    private EntityManager em;

    /**
     * Realiza transferência de valor entre dois benefícios
     * CORREÇÃO DO BUG: Agora com validações, locking e rollback automático
     * 
     * @param fromId ID do benefício de origem
     * @param toId ID do benefício de destino
     * @param amount Valor a ser transferido
     * @throws IllegalArgumentException se parâmetros inválidos
     * @throws InsufficientBalanceException se saldo insuficiente
     * @throws BeneficioNotFoundException se benefício não encontrado
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        // 1. Validações de parâmetros
        validateTransferParameters(fromId, toId, amount);

        // 2. Buscar benefícios com PESSIMISTIC LOCK para evitar race conditions
        Beneficio from = em.find(Beneficio.class, fromId, LockModeType.PESSIMISTIC_WRITE);
        Beneficio to = em.find(Beneficio.class, toId, LockModeType.PESSIMISTIC_WRITE);

        // 3. Validar existência
        if (from == null) {
            throw new BeneficioNotFoundException("Benefício de origem não encontrado: " + fromId);
        }
        if (to == null) {
            throw new BeneficioNotFoundException("Benefício de destino não encontrado: " + toId);
        }

        // 4. Validar se benefícios estão ativos
        if (!from.getAtivo()) {
            throw new IllegalArgumentException("Benefício de origem está inativo: " + fromId);
        }
        if (!to.getAtivo()) {
            throw new IllegalArgumentException("Benefício de destino está inativo: " + toId);
        }

        // 5. Validar saldo suficiente (CORREÇÃO DO BUG PRINCIPAL)
        if (from.getValor().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                String.format("Saldo insuficiente. Disponível: %s, Solicitado: %s", 
                    from.getValor(), amount)
            );
        }

        // 6. Realizar transferência
        from.setValor(from.getValor().subtract(amount));
        to.setValor(to.getValor().add(amount));

        // 7. Persistir alterações (merge automático com locks)
        em.merge(from);
        em.merge(to);

        // Rollback automático em caso de exceção (gerenciado pelo container EJB)
    }

    /**
     * Busca todos os benefícios
     */
    public List<Beneficio> findAll() {
        return em.createQuery("SELECT b FROM Beneficio b ORDER BY b.id", Beneficio.class)
                .getResultList();
    }

    /**
     * Busca benefício por ID
     */
    public Beneficio findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        Beneficio beneficio = em.find(Beneficio.class, id);
        if (beneficio == null) {
            throw new BeneficioNotFoundException("Benefício não encontrado: " + id);
        }
        return beneficio;
    }

    /**
     * Cria novo benefício
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Beneficio create(Beneficio beneficio) {
        if (beneficio == null) {
            throw new IllegalArgumentException("Benefício não pode ser nulo");
        }
        if (beneficio.getId() != null) {
            throw new IllegalArgumentException("Novo benefício não deve ter ID");
        }
        em.persist(beneficio);
        em.flush();
        return beneficio;
    }

    /**
     * Atualiza benefício existente
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Beneficio update(Beneficio beneficio) {
        if (beneficio == null || beneficio.getId() == null) {
            throw new IllegalArgumentException("Benefício e ID não podem ser nulos");
        }
        Beneficio existing = findById(beneficio.getId());
        return em.merge(beneficio);
    }

    /**
     * Remove benefício (soft delete - marca como inativo)
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(Long id) {
        Beneficio beneficio = findById(id);
        beneficio.setAtivo(false);
        em.merge(beneficio);
    }

    /**
     * Valida parâmetros da transferência
     */
    private void validateTransferParameters(Long fromId, Long toId, BigDecimal amount) {
        if (fromId == null) {
            throw new IllegalArgumentException("ID de origem não pode ser nulo");
        }
        if (toId == null) {
            throw new IllegalArgumentException("ID de destino não pode ser nulo");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Valor não pode ser nulo");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Não é possível transferir para o mesmo benefício");
        }
    }

    // Exceções customizadas
    public static class InsufficientBalanceException extends RuntimeException {
        public InsufficientBalanceException(String message) {
            super(message);
        }
    }

    public static class BeneficioNotFoundException extends RuntimeException {
        public BeneficioNotFoundException(String message) {
            super(message);
        }
    }
}

