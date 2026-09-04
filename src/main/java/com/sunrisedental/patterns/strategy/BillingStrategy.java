package com.sunrisedental.patterns.strategy;

import java.math.BigDecimal;

/**
 * Strategy Pattern Interface
 * ----------------------------
 * Defines a contract for different billing calculation algorithms.
 * New billing rules (e.g. insurance, senior citizen discount, corporate
 * plans) can be added as new classes WITHOUT modifying existing code
 * (Open/Closed Principle).
 */
public interface BillingStrategy {
    BigDecimal calculateTotal(BigDecimal consultationFee, BigDecimal treatmentCost, BigDecimal discount);
    String getStrategyName();
}