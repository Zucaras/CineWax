package org.devedgardoff.methods;

import org.devedgardoff.models.Patient;

class Node {
    Patient patient;
    Node prev;
    Node next;

    public Node(Patient patient) {
        this.patient = patient;
        this.prev = null;
        this.next = null;
    }
}

public class PatientManager {
    private Node head;
    private Node tail;

    public boolean isEmpty() {
        return head == null;
    }

    // Requirement 1: Register patient with ordered insertion (highest severity first)
    public void registerPatient(Patient newPatient) {
        Node newNode = new Node(newPatient);

        if (isEmpty()) {
            head = tail = newNode;
        } else if (newPatient.getSeverity() > head.patient.getSeverity()) {
            // New patient is more critical than current head
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        } else {
            Node current = head;
            // Traverse to find the correct spot (ordered by severity)
            while (current != null && current.patient.getSeverity() >= newPatient.getSeverity()) {
                current = current.next;
            }

            if (current == null) {
                // Insert at the end (least critical)
                tail.next = newNode;
                newNode.prev = tail;
                tail = newNode;
            } else {
                // Insert in the middle
                newNode.next = current;
                newNode.prev = current.prev;
                current.prev.next = newNode;
                current.prev = newNode;
            }
        }
        System.out.println("Paciente registrado exitosamente.");
    }

    // Requirement 2: Attend patient with highest severity (remove head)
    public void attendPatient() {
        if (isEmpty()) {
            System.out.println("No hay pacientes en la lista de espera.");
            return;
        }

        System.out.println("Atendiendo al paciente mas critico:");
        System.out.println(head.patient.toString());

        if (head == tail) {
            head = tail = null;
        } else {
            head = head.next;
            head.prev = null;
        }
        System.out.println("Paciente retirado de la lista.");
    }

    // Requirement 3: Show list (Forward)
    public void displayOrdered() {
        if (isEmpty()) {
            System.out.println("La lista de espera esta vacia.");
            return;
        }
        System.out.println("Lista de pacientes (Mayor a Menor gravedad):");
        Node current = head;
        while (current != null) {
            System.out.println("- " + current.patient.toString());
            current = current.next;
        }
    }

    // Requirement 3: Show list (Backward)
    public void displayReverse() {
        if (isEmpty()) {
            System.out.println("La lista de espera esta vacia.");
            return;
        }
        System.out.println("Lista de pacientes (Menor a Mayor gravedad):");
        Node current = tail;
        while (current != null) {
            System.out.println("- " + current.patient.toString());
            current = current.prev;
        }
    }
}