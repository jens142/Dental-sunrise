package com.sunrisedental.patterns.strategy;

import java.math.BigDecimal;

/**
 * Insurance billing: consultation fee is waived (covered by insurer),
 * only 70% of treatment cost is charged to the patient (30% co-pay
 * covered separately in real insurance workflows).
 */
public class InsuranceBillingStrategy implements BillingStrategy {

    private static final BigDecimal PATIENT_SHARE = new BigDecimal("0.70");

    @Override
    public BigDecimal calculateTotal(BigDecimal consultationFee, BigDecimal treatmentCost, BigDecimal discount) {
        BigDecimal total = treatmentCost.multiply(PATIENT_SHARE);
        if (discount != null) {
            total = total.subtract(discount);
        }
        return total.max(BigDecimal.ZERO);
    }

    @Override
    public String getStrategyName() {
        return "Insurance Billing (70% Patient Share)";
    }
}