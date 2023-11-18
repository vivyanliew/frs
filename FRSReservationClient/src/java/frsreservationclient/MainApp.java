/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package frsreservationclient;

import ejb.session.stateless.AirportSessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.FlightSchedulePlanSessionBeanRemote;
import ejb.session.stateless.FlightScheduleSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import entity.Airport;
import entity.CabinClass;
import entity.Customer;
import entity.Flight;
import entity.FlightSchedule;
import entity.FlightSchedulePlan;
import entity.SeatInventory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import util.exception.CustomerAlreadyExistsException;
import util.exception.FlightNotFoundException;
import util.exception.FlightScheduleNotFoundException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author liewvivyan
 */
public class MainApp {

    private CustomerSessionBeanRemote customerSessionBeanRemote;
    private Customer currentCustomer;

    private AirportSessionBeanRemote airportSessionBeanRemote;
    private FlightSessionBeanRemote flightSessionBeanRemote;
    private FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBeanRemote;
    private FlightScheduleSessionBeanRemote flightScheduleSessionBeanRemote;

    public MainApp() {
    }

    public MainApp(CustomerSessionBeanRemote customerSessionBeanRemote, AirportSessionBeanRemote airportSessionBeanRemote, FlightSessionBeanRemote flightSessionBeanRemote, FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBeanRemote, FlightScheduleSessionBeanRemote flightScheduleSessionBeanRemote) {
        this();
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.airportSessionBeanRemote = airportSessionBeanRemote;
        this.flightSessionBeanRemote = flightSessionBeanRemote;
        this.flightSchedulePlanSessionBeanRemote = flightSchedulePlanSessionBeanRemote;
        this.flightScheduleSessionBeanRemote = flightScheduleSessionBeanRemote;
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
                try {
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
        String originAirportCode = "";
        String destinationAirportCode = "";
        
        System.out.println("***Search for flight");
        System.out.println("1: One-way flight");
        System.out.println("2: Round-trip");
        response = sc.nextInt();
        sc.nextLine();
        System.out.println("Enter departure airport IATA code> ");
        originAirportCode = sc.nextLine().trim();
        Airport originAirport = new Airport();
        originAirport = airportSessionBeanRemote.retrieveAirportByIATACode(originAirportCode);
        
        System.out.println("Enter destination airport IATA code> ");
        destinationAirportCode = sc.nextLine().trim();
        Airport destinationAirport = airportSessionBeanRemote.retrieveAirportByIATACode(destinationAirportCode);
        System.out.print("Enter departure date (d MMM yy) (eg. 1 Jan 23) > ");
        String departDate = sc.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yy");
        LocalDate localDate = LocalDate.parse(departDate, formatter);
        LocalDateTime localDateTimeDepartDate = localDate.atStartOfDay(); //12am
        System.out.println("Enter number of passsengers> ");
        Integer numberOfPassengers = sc.nextInt();

        System.out.println("Enter preference for flight> ");
        System.out.println("0: No preference");
        System.out.println("1: Direct Flight ");
        System.out.println("2: Connecting Flight ");
        System.out.println("> ");
        Integer flightTypeResponse = 0;
        flightTypeResponse = sc.nextInt();
        sc.nextLine();

        String cabinClassPref = "";
        System.out.println("Enter preference for cabin class> ");
        System.out.println("A = No preference");
        System.out.println("F = First class");
        System.out.println("J = Business class");
        System.out.println("W = Premium economy");
        System.out.println("Y = Economy");
        cabinClassPref = sc.nextLine();

        if (flightTypeResponse == 0) {
            
           
      
            
//            List<Flight> flightsFound = new ArrayList<>();
//            try {
//                flightsFound = flightSessionBeanRemote.getFlightByOD(originAirport, destinationAirport);
//            } catch (FlightNotFoundException ex) {
//                System.out.println(ex.getMessage());
//                System.out.println("Try again!");
//            }

            //List<List<FlightSchedule>> flightSchedules = new ArrayList<List<FlightSchedule>>(flightsFound.size());
            try {
                List<FlightSchedule> flightSchedulesFound = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate, cabinClassPref);
                List<FlightSchedule> flightSchedulesAddOneDay = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.plusDays(1), cabinClassPref);
                List<FlightSchedule> flightSchedulesAddTwoDay = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.plusDays(2), cabinClassPref);
                List<FlightSchedule> flightSchedulesAddThreeDays = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.plusDays(3), cabinClassPref);

                List<FlightSchedule> flightSchedulesMinusOneDay = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.minusDays(1), cabinClassPref);
                List<FlightSchedule> flightSchedulesMinusTwoDays = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.minusDays(2), cabinClassPref);
                List<FlightSchedule> flightSchedulesMinusThreeDays = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.minusDays(3), cabinClassPref);

                System.out.println("                      ============= Available Direct Outbound Flights ============= ");
                System.out.println("                             ============ On Desired Date =========== ");
                printSingleFlightSchedule(flightSchedulesFound,cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 1 day before Desired Date ============ ");
                printSingleFlightSchedule(flightSchedulesMinusOneDay,cabinClassPref,numberOfPassengers);
                
            } catch (FlightNotFoundException ex) {
                System.out.print("Sorry, there are no flights with your desired flight route\n");
            } catch (FlightScheduleNotFoundException ex) {
                System.out.println(ex.getMessage());
            }

//            for (int i = 0; i < flightsFound.size(); i++) {
//                flightSchedulesFound = flightSchedulePlanSessionBeanRemote.retrieveSchedulesForFlight(flightsFound.get(i), localDateTimeDepartDate);
//                flightSchedules.add(i, flightSchedulesFound);
//
//            }
//            for (int i = 0; i < flightSchedules.size(); i++) {
//                if (flightSchedules.get(i).size() != 0) {
//                    System.out.println("Flight " + flightsFound.get(i).getFlightNumber() + ": ");
//                    System.out.println("Flight schedules on departure date: ");
//                    for (int j = 0; j < flightSchedules.get(i).size(); j++) {
//                        System.out.println(flightSchedules.get(i).get(j).getDepartureDateTime());
//                    }
//                }
//
//            }
        }

        if (response == 1) {

        } else {
//            System.out.println("Enter departure airport> ");
//            String originAirportName = sc.nextLine().trim();
//            System.out.println("Enter destination airport> ");
//            String destinationAirportName = sc.nextLine().trim();
//            System.out.println("Enter departure date> ");
//            String departureDate = sc.nextLine().trim();
//            System.out.println("Enter return date> ");
//            String returnDate = sc.nextLine().trim();
//            System.out.println("Enter number of passsengers> ");
//            Integer numberOfPassengers = sc.nextInt();
        }
    }

    void registerAsCustomer() throws CustomerAlreadyExistsException {
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
    
    void printSingleFlightSchedule(List<FlightSchedule> flightSchedules, String cabinPref, int passengers) throws FlightScheduleNotFoundException {
        System.out.printf("%15s%20s%30s%30s%40s%20s%20s%20s%30s%25s%25s\n", "Flight ID", 
                        "Flight Number", 
                        "Departure Airport", 
                        "Arrival Airport", 
                        "Departure Date & Time", 
                        "Duration (HRS)", 
                        "Arrival Date & Time", 
                        "Cabin Type", 
                        //"Number of Seats Balanced", 
                        "Price per head", 
                        "Total Price"); 
        for (FlightSchedule flightSchedule: flightSchedules) {
            LocalDateTime arrivalTime = flightSchedule.getArrivalDateTime();
            SeatInventory seatInventory = flightSchedule.getSeatInventory();
            for (CabinClass cabinClass : seatInventory.getAllCabinClasses()) {
                String cabinClassType;
                if (cabinClass.getCabinClassName().equals(cabinPref) && (cabinClass.getCabinClassName().equals("F") || cabinPref.equals("A"))) {
                    cabinClassType = "First Class";
                } else if (cabinClass.getCabinClassName().equals(cabinPref) && (cabinClass.getCabinClassName().equals("J") || cabinPref.equals("A"))) {
                    cabinClassType = "Business Class";
                } else  if (cabinClass.getCabinClassName().equals(cabinPref) && (cabinClass.getCabinClassName().equals("W") || cabinPref.equals("A"))) {
                    cabinClassType = "Premium Economy Class";
                } else if (cabinClass.getCabinClassName().equals(cabinPref) && (cabinClass.getCabinClassName().equals("Y") || cabinPref.equals("A"))) {
                    cabinClassType = "Economy Class";
                } else {
                    continue;
                }
                System.out.printf("%15s%20s%30s%30s%40s%20s%20s%20s%30s%25s%25s\n", flightSchedule.getFlightScheduleId(),
                        flightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber(),
                        flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getOriginAirport().getAirportName(),
                        flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getDestinationAirport().getAirportName(),
                        flightSchedule.getDepartureDateTime().toString().substring(0, 19),
                        flightSchedule.getFlightDurationHours(),
                        arrivalTime.toString().substring(0, 19),
                        cabinClassType,
                        //seats.getBalance(),
                        flightScheduleSessionBeanRemote.getSmallestFare(flightSchedule, cabinClass.getCabinClassName()).getFareAmount(),
                        flightScheduleSessionBeanRemote.getSmallestFare(flightSchedule, cabinClass.getCabinClassName()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                );

            }
        }
    }

}
