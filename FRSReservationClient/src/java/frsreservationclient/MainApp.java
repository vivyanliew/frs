/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package frsreservationclient;

import ejb.session.stateless.CustomerSessionBeanRemote;
import entity.Customer;
import java.util.Scanner;
import util.exception.CustomerAlreadyExistsException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author liewvivyan
 */
public class MainApp {

    private CustomerSessionBeanRemote customerSessionBeanRemote;
    private Customer currentCustomer;

    public MainApp() {
    }

    public MainApp(CustomerSessionBeanRemote customerSessionBeanRemote) {
        this();
        this.customerSessionBeanRemote = customerSessionBeanRemote;
    }

    public void runApp() {
        Scanner sc = new Scanner(System.in);
        boolean loggedIn = false;
        Integer response = 0;
        String toContinue = "";

        while (!loggedIn) {
            System.out.println("***Welcome to Merlion Airlines FRS Reservation");
            System.out.println("1: Register as customer");
            System.out.println("2: Login");
            System.out.println("3: Search Flight");
            response = sc.nextInt();
            if (response == 1) {
                try{
                    registerAsCustomer();
                    System.out.println("Customer created successfully");
                    loggedIn = true;
                customerHomePage();
                } catch (CustomerAlreadyExistsException ex) {
                    System.out.println("Customer already exists : " + ex.getMessage() + "\n");
                }
                
            } else if (response == 2) {
                try {
                    doLogin();
                    System.out.println("Log-in successful!");
                    loggedIn = true;
                    customerHomePage();
                } catch (InvalidLoginCredentialException ex) {
                    System.out.println("Invalid login credential : " + ex.getMessage() + "\n");
                }

            } else if (response == 3) {
                searchFlight();
            }
            System.out.println("Do you want to continue?");
            System.out.println("Press Y to continue");
            sc.nextLine();
            toContinue = sc.nextLine();
            if (toContinue.equals("Y") || toContinue.equals("y")) {
                loggedIn = false;
            } else {
                loggedIn = true;
            }
        }
    }

    void customerHomePage() {
    }

    void searchFlight() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        System.out.println("***Search for flight");
        System.out.println("1: One-way flight");
        System.out.println("2: Round-trip");
        if (response == 1) {
            System.out.println("Enter departure airport> ");
            String originAirportName = sc.nextLine().trim();
            System.out.println("Enter destination airport> ");
            String destinationAirportName = sc.nextLine().trim();
            System.out.println("Enter departure date> ");
            String departureDate = sc.nextLine().trim();
            System.out.println("Enter number of passsengers> ");
            Integer numberOfPassengers = sc.nextInt();
        } else {
            System.out.println("Enter departure airport> ");
            String originAirportName = sc.nextLine().trim();
            System.out.println("Enter destination airport> ");
            String destinationAirportName = sc.nextLine().trim();
            System.out.println("Enter departure date> ");
            String departureDate = sc.nextLine().trim();
            System.out.println("Enter return date> ");
            String returnDate = sc.nextLine().trim();
            System.out.println("Enter number of passsengers> ");
            Integer numberOfPassengers = sc.nextInt();
        }
    }

    void registerAsCustomer() throws CustomerAlreadyExistsException{
        Scanner sc = new Scanner(System.in);
        System.out.println("***Register as customer");
        System.out.println("Enter first name> ");
        String firstName = sc.nextLine();
        System.out.println("Enter last name> ");
        String lastName = sc.nextLine();
        System.out.println("Enter email address> ");
        String email = sc.nextLine();
        System.out.println("Enter phone number> ");
        String phoneNum = sc.nextLine();
        System.out.println("Enter address> ");
        String address = sc.nextLine();
        System.out.println("Enter password> ");
        String password = sc.nextLine();
        Customer newCustomer = new Customer(firstName, lastName, email, phoneNum, address, password);
        newCustomer = customerSessionBeanRemote.createCustomer(newCustomer);

    }

    void doLogin() throws InvalidLoginCredentialException {
        Scanner sc = new Scanner(System.in);
        System.out.println("***Login");
        String email = "";
        String password = "";

        System.out.println("Enter email > ");
        email = sc.nextLine().trim();
        System.out.println("Enter password > ");
        password = sc.nextLine().trim();
        currentCustomer = customerSessionBeanRemote.customerLogin(email, password);
    }

}
