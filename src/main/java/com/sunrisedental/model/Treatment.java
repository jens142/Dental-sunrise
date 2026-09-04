package com.sunrisedental.model;

import java.math.BigDecimal;

public class Treatment {

    private int treatmentId;
    private String treatmentName;
    private String description;
    private BigDecimal baseCost;
    private BigDecimal consultationFee;
    private boolean active;

    public Treatment() {}

    public Treatment(int treatmentId, String treatmentName, String description,
                      BigDecimal baseCost, BigDecimal consultationFee, boolean active) {
        this.treatmentId = treatmentId;
        this.treatmentName = treatmentName;
        this.description = description;
        this.baseCost = baseCost;
        this.consultationFee = consultationFee;
        this.active = active;
    }

    public int getTreatmentId() { return treatmentId; }
    public void setTreatmentId(int treatmentId) { this.treatmentId = treatmentId; }

    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getBaseCost() { return baseCost; }
    public void setBaseCost(BigDecimal baseCost) { this.baseCost = baseCost; }

    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}