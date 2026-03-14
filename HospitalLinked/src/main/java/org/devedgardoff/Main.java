package org.devedgardoff;

import org.devedgardoff.methods.PatientManager;
import org.devedgardoff.models.Patient;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        PatientManager manager = new PatientManager();
        boolean active = true;

        System.out.println("=== HOSPITAL DE OCCIDENTE - GESTION DE URGENCIAS ===");

        while (active) {
            try {
                System.out.println("\n--- MENU ---");
                System.out.println("1. Registrar nuevo paciente");
                System.out.println("2. Atender paciente (Mas grave)");
                System.out.println("3. Mostrar lista de espera");
                System.out.println("0. Salir");
                System.out.print("Opcion: ");

                int option = sc.nextInt(); sc.nextLine();

                switch (option) {
                    case 1:
                        System.out.print("Nombre completo: ");
                        String name = sc.nextLine();
                        System.out.print("Numero de seguro: ");
                        int insurance = sc.nextInt();
                        System.out.print("Edad: ");
                        int age = sc.nextInt();
                        System.out.print("Nivel de gravedad (1-5): ");
                        int severity = sc.nextInt();

                        if (severity < 1 || severity > 5) {
                            System.out.println("Error: La gravedad debe ser entre 1 y 5.");
                        } else {
                            manager.registerPatient(new Patient(name, insurance, age, severity));
                        }
                        break;
                    case 2:
                        manager.attendPatient();
                        break;
                    case 3:
                        System.out.println("1. Orden descendente | 2. Orden ascendente");
                        int order = sc.nextInt();
                        if (order == 1) manager.displayOrdered();
                        else manager.displayReverse();
                        break;
                    case 0:
                        active = false;
                        System.out.println("Cerrando sistema hospitalario.");
                        break;
                    default:
                        System.out.println("Opcion no valida.");
                }
            } catch (Exception e) {
                System.out.println("Error: Entrada invalida.");
                sc.nextLine();
            }
        }
        sc.close();
    }
}