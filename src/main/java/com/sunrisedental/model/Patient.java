package com.sunrisedental.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Patient {

    public enum Gender {
        Male, Female, Other
    }

    private int patientId;
    private String patientName;
    private String address;
    private String contactNumber;
    private String email;
    private Date dateOfBirth;
    private Gender gender;
    private String allergies;
    private Timestamp registeredOn;

    public Patient() {}

    public Patient(int patientId, String patientName, String address, String contactNumber,
                   String email, Date dateOfBirth, Gender gender, String allergies,
                   Timestamp registeredOn) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.allergies = allergies;
        this.registeredOn = registeredOn;
    }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public Timestamp getRegisteredOn() { return registeredOn; }
    public void setRegisteredOn(Timestamp registeredOn) { this.registeredOn = registeredOn; }
}