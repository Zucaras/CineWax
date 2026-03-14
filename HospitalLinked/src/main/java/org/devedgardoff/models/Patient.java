package org.devedgardoff.models;

public class Patient {
    private String fullName;
    private int insuranceNumber;
    private int age;
    private int severity; // 1 (mild) to 5 (critical)

    public Patient(String fullName, int insuranceNumber, int age, int severity) {
        this.fullName = fullName;
        this.insuranceNumber = insuranceNumber;
        this.age = age;
        this.severity = severity;
    }

    // Getters for the logic
    public int getSeverity() { return severity; }
    
    @Override
    public String toString() {
        return "Nombre: " + fullName + " | Seguro: " + insuranceNumber + 
               " | Edad: " + age + " | Gravedad: " + severity;
    }
}