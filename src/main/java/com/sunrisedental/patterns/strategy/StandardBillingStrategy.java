package com.sunrisedental.patterns.strategy;

import java.math.BigDecimal;

/**
 * Standard billing: consultation fee + treatment cost - discount.
 * Applies to regular walk-in patients.
 */
public class StandardBillingStrategy implements BillingStrategy {

    @Override
    public BigDecimal calculateTotal(BigDecimal consultationFee, BigDecimal treatmentCost, BigDecimal discount) {
        BigDecimal total = consultationFee.add(treatmentCost);
        if (discount != null) {
            total = total.subtract(discount);
        }
        return total.max(BigDecimal.ZERO);
    }

    @Override
    public String getStrategyName() {
        return "Standard Billing";
    }
}