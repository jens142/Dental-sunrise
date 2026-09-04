package com.sunrisedental.model;

public class Dentist {

    private int dentistId;
    private String dentistName;
    private String specialization;
    private String contactNumber;
    private String email;
    private String workingDays;
    private String workingHours;
    private boolean active;

    public Dentist() {}

    public Dentist(int dentistId, String dentistName, String specialization,
                    String contactNumber, String email, String workingDays,
                    String workingHours, boolean active) {
        this.dentistId = dentistId;
        this.dentistName = dentistName;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        this.email = email;
        this.workingDays = workingDays;
        this.workingHours = workingHours;
        this.active = active;
    }

    public int getDentistId() { return dentistId; }
    public void setDentistId(int dentistId) { this.dentistId = dentistId; }

    public String getDentistName() { return dentistName; }
    public void setDentistName(String dentistName) { this.dentistName = dentistName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getWorkingDays() { return workingDays; }
    public void setWorkingDays(String workingDays) { this.workingDays = workingDays; }

    public String getWorkingHours() { return workingHours; }
    public void setWorkingHours(String workingHours) { this.workingHours = workingHours; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}