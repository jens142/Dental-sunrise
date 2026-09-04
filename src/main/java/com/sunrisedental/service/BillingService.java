package com.sunrisedental.service;

import com.sunrisedental.dao.BillDAO;
import com.sunrisedental.model.Bill;
import com.sunrisedental.patterns.strategy.BillingContext;
import com.sunrisedental.patterns.strategy.BillingStrategy;
import com.sunrisedental.patterns.strategy.InsuranceBillingStrategy;
import com.sunrisedental.patterns.strategy.StandardBillingStrategy;

import java.math.BigDecimal;
import java.util.List;

/**
 * Selects the correct Strategy at runtime based on patient billing
 * category, then delegates the calculation to it before persisting
 * via BillDAO (which itself calls sp_calculate_bill).
 */
public class BillingService {

    public enum BillingCategory {
        STANDARD, INSURANCE
    }

    private final BillDAO billDAO = new BillDAO();

    public Bill generateBill(int appointmentId, BigDecimal discount, BillingCategory category) {
        if (appointmentId <= 0) {
            throw new IllegalArgumentException("Valid appointment id is required.");
        }
        if (discount != null && discount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Discount cannot be negative.");
        }

        // Strategy pattern in action - the caller never needs to know
        // which concrete algorithm ran, only which category was chosen.
        BillingStrategy strategy = resolveStrategy(category);
        BillingContext billingContext = new BillingContext(strategy);

        // sp_calculate_bill computes/persists consultationFee + treatmentCost
        // at the DB layer; we re-apply the Strategy's total here so the
        // chosen billing category is reflected in the amount charged.
        Bill dbCalculated = billDAO.calculateAndSaveBill(appointmentId, discount);
        if (dbCalculated == null) {
            throw new RuntimeException("Failed to calculate bill for appointment " + appointmentId);
        }

        BigDecimal finalTotal = billingContext.executeCalculation(
                dbCalculated.getConsultationFee(), dbCalculated.getTreatmentCost(), discount);
        dbCalculated.setTotalAmount(finalTotal);

        return dbCalculated;
    }

    public Bill getBillForAppointment(int appointmentId) {
        return billDAO.findByAppointmentId(appointmentId);
    }

    public Bill getBillById(int billId) {
        return billDAO.findById(billId);
    }

    public List<Bill> getAllBills() {
        return billDAO.getAllBills();
    }

    public boolean markPaid(int billId) {
        if (billId <= 0) {
            throw new IllegalArgumentException("Valid bill id is required.");
        }
        return billDAO.markAsPaid(billId);
    }

    private BillingStrategy resolveStrategy(BillingCategory category) {
        if (category == BillingCategory.INSURANCE) {
            return new InsuranceBillingStrategy();
        }
        return new StandardBillingStrategy();
    }
}