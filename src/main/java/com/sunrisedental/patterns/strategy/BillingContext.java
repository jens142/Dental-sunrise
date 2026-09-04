package com.sunrisedental.patterns.strategy;

import java.math.BigDecimal;

/**
 * Strategy Pattern Context
 * --------------------------
 * Holds a reference to a BillingStrategy and delegates calculation to it.
 * The BillingService decides WHICH strategy to inject at runtime based
 * on the patient's billing category.
 */
public class BillingContext {

    private BillingStrategy strategy;

    public BillingContext(BillingStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(BillingStrategy strategy) {
        this.strategy = strategy;
    }

    public BigDecimal executeCalculation(BigDecimal consultationFee, BigDecimal treatmentCost, BigDecimal discount) {
        return strategy.calculateTotal(consultationFee, treatmentCost, discount);
    }

    public String getActiveStrategyName() {
        return strategy.getStrategyName();
    }
}