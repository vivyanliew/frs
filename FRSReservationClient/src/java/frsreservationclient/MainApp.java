/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package frsreservationclient;

import ejb.session.stateless.AirportSessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.FlightReservationSessionBeanRemote;
import ejb.session.stateless.FlightSchedulePlanSessionBeanRemote;
import ejb.session.stateless.FlightScheduleSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import ejb.session.stateless.SeatInventorySessionBeanRemote;
import entity.Airport;
import entity.CabinClass;
import entity.Customer;
import entity.Fare;
import entity.Flight;
import entity.FlightReservation;
import entity.FlightSchedule;
import entity.FlightSchedulePlan;
import entity.Passenger;
import entity.SeatInventory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javafx.util.Pair;
import util.exception.CustomerAlreadyExistsException;
import util.exception.FlightNotFoundException;
import util.exception.FlightScheduleNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.SeatInventoryNotFoundException;

/**
 *
 * @author liewvivyan
 */
public class MainApp {

    private CustomerSessionBeanRemote customerSessionBeanRemote;
    private Customer currentCustomer;
    private boolean loggedIn;

    private AirportSessionBeanRemote airportSessionBeanRemote;
    private FlightSessionBeanRemote flightSessionBeanRemote;
    private FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBeanRemote;
    private FlightScheduleSessionBeanRemote flightScheduleSessionBeanRemote;
    private SeatInventorySessionBeanRemote seatInventorySessionBeanRemote;
    private FlightReservationSessionBeanRemote flightReservationSessionBeanRemote;

    public MainApp() {
    }

    public MainApp(CustomerSessionBeanRemote customerSessionBeanRemote, AirportSessionBeanRemote airportSessionBeanRemote, FlightSessionBeanRemote flightSessionBeanRemote, FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBeanRemote, FlightScheduleSessionBeanRemote flightScheduleSessionBeanRemote, SeatInventorySessionBeanRemote seatInventorySessionBeanRemote,FlightReservationSessionBeanRemote flightReservationSessionBeanRemote) {
        this();
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.airportSessionBeanRemote = airportSessionBeanRemote;
        this.flightSessionBeanRemote = flightSessionBeanRemote;
        this.flightSchedulePlanSessionBeanRemote = flightSchedulePlanSessionBeanRemote;
        this.flightScheduleSessionBeanRemote = flightScheduleSessionBeanRemote;
        this.seatInventorySessionBeanRemote = seatInventorySessionBeanRemote;
        this.flightReservationSessionBeanRemote = flightReservationSessionBeanRemote;
    }

    public void runApp() throws FlightScheduleNotFoundException {
        Scanner sc = new Scanner(System.in);
        //boolean loggedIn = false;
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
                try {
                    searchFlight();
                } catch (SeatInventoryNotFoundException ex) {
                    System.out.println(ex.getMessage());
                }
                
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

    void searchFlight() throws FlightScheduleNotFoundException, SeatInventoryNotFoundException {
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
        Airport originAirport;
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
        System.out.println("1: No Preference "); //show direct and connecting
        System.out.println("2: Direct Flight ");
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

        if (flightTypeResponse == 1) {

            try {
                List<FlightSchedule> flightSchedulesFound = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate, cabinClassPref);
                List<FlightSchedule> flightSchedulesAddOneDay = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.plusDays(1), cabinClassPref);
                List<FlightSchedule> flightSchedulesAddTwoDays = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.plusDays(2), cabinClassPref);
                List<FlightSchedule> flightSchedulesAddThreeDays = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.plusDays(3), cabinClassPref);

                List<FlightSchedule> flightSchedulesMinusOneDay = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.minusDays(1), cabinClassPref);
                List<FlightSchedule> flightSchedulesMinusTwoDays = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.minusDays(2), cabinClassPref);
                List<FlightSchedule> flightSchedulesMinusThreeDays = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.minusDays(3), cabinClassPref);

                System.out.println("                      ============= Available Direct Outbound Flights ============= ");
                System.out.println("                             ============ On Actual Date =========== ");
                printSingleFlightSchedule(flightSchedulesFound, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 1 day before Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesMinusOneDay, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 days before Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesMinusTwoDays, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 days before Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesMinusThreeDays, cabinClassPref, numberOfPassengers);

                System.out.println("\n                  ============ Departing 1 day after Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesAddOneDay, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 days after Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesAddTwoDays, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 days after Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesAddThreeDays, cabinClassPref, numberOfPassengers);
            } catch (FlightNotFoundException ex) {
                System.out.print("Sorry, there are no flights with your desired flight route\n");
            } catch (FlightScheduleNotFoundException ex) {
                System.out.println(ex.getMessage());
            }

            String departAirportCode = originAirportCode;
            String destination = destinationAirportCode;
            try {
                List<Pair<FlightSchedule, FlightSchedule>> dateOfActualOutbound = flightScheduleSessionBeanRemote.getIndirectFlightSchedules(departAirportCode, destinationAirportCode, localDateTimeDepartDate, cabinClassPref);
                List<Pair<FlightSchedule, FlightSchedule>> oneDayAfterActualOutbound = flightScheduleSessionBeanRemote.getIndirectFlightSchedules(departAirportCode, destinationAirportCode, localDateTimeDepartDate.plusDays(1), cabinClassPref);

                List<Pair<FlightSchedule, FlightSchedule>> twoDaysAfterActualOutbound = flightScheduleSessionBeanRemote.getIndirectFlightSchedules(departAirportCode, destinationAirportCode, localDateTimeDepartDate.plusDays(2), cabinClassPref);

                List<Pair<FlightSchedule, FlightSchedule>> threeDaysAfterActualOutbound = flightScheduleSessionBeanRemote.getIndirectFlightSchedules(departAirportCode, destinationAirportCode, localDateTimeDepartDate.plusDays(3), cabinClassPref);
                List<Pair<FlightSchedule, FlightSchedule>> oneDayBeforeActualOutbound = flightScheduleSessionBeanRemote.getIndirectFlightSchedules(departAirportCode, destinationAirportCode, localDateTimeDepartDate.minusDays(1), cabinClassPref);
                List<Pair<FlightSchedule, FlightSchedule>> twoDaysBeforeActualOutbound = flightScheduleSessionBeanRemote.getIndirectFlightSchedules(departAirportCode, destinationAirportCode, localDateTimeDepartDate.minusDays(2), cabinClassPref);

                List<Pair<FlightSchedule, FlightSchedule>> threeDaysBeforeActualOutbound = flightScheduleSessionBeanRemote.getIndirectFlightSchedules(departAirportCode, destinationAirportCode, localDateTimeDepartDate.minusDays(3), cabinClassPref);

                System.out.println("                      ============= Available Outbound Flights ============= ");

                System.out.println("                             ============ On Desired Date =========== ");
                printFlightScheduleWithConnecting(dateOfActualOutbound, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 1 day before Actual Date ============ ");
                printFlightScheduleWithConnecting(oneDayBeforeActualOutbound, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 days before Actual Date ============ ");
                printFlightScheduleWithConnecting(twoDaysBeforeActualOutbound, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 days before Actual Date ============ ");
                printFlightScheduleWithConnecting(twoDaysBeforeActualOutbound, cabinClassPref, numberOfPassengers);

                System.out.println("\n                  ============ Departing 1 day after Actual Date ============ ");
                printFlightScheduleWithConnecting(oneDayAfterActualOutbound, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 days after Actual Date ============ ");
                printFlightScheduleWithConnecting(twoDaysBeforeActualOutbound, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 days after Actual Date ============ ");
                printFlightScheduleWithConnecting(threeDaysBeforeActualOutbound, cabinClassPref, numberOfPassengers);

            } catch (FlightNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (FlightScheduleNotFoundException ex) {
                System.out.println(ex.getMessage());
            }

        }
        if (flightTypeResponse == 2) {
            try {
                List<FlightSchedule> flightSchedulesFound = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate, cabinClassPref);
                List<FlightSchedule> flightSchedulesAddOneDay = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.plusDays(1), cabinClassPref);
                List<FlightSchedule> flightSchedulesAddTwoDays = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.plusDays(2), cabinClassPref);
                List<FlightSchedule> flightSchedulesAddThreeDays = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.plusDays(3), cabinClassPref);

                List<FlightSchedule> flightSchedulesMinusOneDay = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.minusDays(1), cabinClassPref);
                List<FlightSchedule> flightSchedulesMinusTwoDays = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.minusDays(2), cabinClassPref);
                List<FlightSchedule> flightSchedulesMinusThreeDays = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.minusDays(3), cabinClassPref);

                System.out.println("                      ============= Available Direct Outbound Flights ============= ");
                System.out.println("                             ============ On Actual Date =========== ");
                printSingleFlightSchedule(flightSchedulesFound, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 1 day before Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesMinusOneDay, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 days before Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesMinusTwoDays, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 days before Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesMinusThreeDays, cabinClassPref, numberOfPassengers);

                System.out.println("\n                  ============ Departing 1 day after Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesAddOneDay, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 days after Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesAddTwoDays, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 days after Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesAddThreeDays, cabinClassPref, numberOfPassengers);
            } catch (FlightNotFoundException ex) {
                System.out.print("Sorry, there are no flights with your desired flight route\n");
            } catch (FlightScheduleNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        }

        if (response == 2 && flightTypeResponse == 1) {//return trip - no preference

            System.out.println("Enter return date> ");
            String returnDate = sc.nextLine();
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yy");
            LocalDate localDate1 = LocalDate.parse(returnDate, formatter);
            LocalDateTime localDateTimeReturnDate = localDate1.atStartOfDay(); //12am

            try {

                List<FlightSchedule> returnFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate, cabinClassPref);
                List<FlightSchedule> returnOneDayBeforeFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.minusDays(1), cabinClassPref);
                List<FlightSchedule> returnTwoDaysBeforeFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.minusDays(2), cabinClassPref);
                List<FlightSchedule> returnThreeDaysBeforeFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.minusDays(3), cabinClassPref);
                List<FlightSchedule> returnOneDayAfterFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.plusDays(1), cabinClassPref);
                List<FlightSchedule> returnTwoDaysAfterFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.plusDays(2), cabinClassPref);
                List<FlightSchedule> returnThreeDaysAfterFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.plusDays(3), cabinClassPref);

                System.out.println("                      ============= Available Direct Inbound Flights ============= ");
                System.out.println("                             ============ On Actual Date =========== ");
                printSingleFlightSchedule(returnFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 1 day before Actual Date ============ ");
                printSingleFlightSchedule(returnOneDayBeforeFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 days before Actual Date ============ ");
                printSingleFlightSchedule(returnTwoDaysBeforeFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 days before Actual Date ============ ");
                printSingleFlightSchedule(returnThreeDaysBeforeFlightSchedules, cabinClassPref, numberOfPassengers);

                System.out.println("\n                  ============ Departing 1 day after Actual Date ============ ");
                printSingleFlightSchedule(returnOneDayAfterFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 days after Actual Date ============ ");
                printSingleFlightSchedule(returnTwoDaysAfterFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 days after Actual Date ============ ");
                printSingleFlightSchedule(returnThreeDaysAfterFlightSchedules, cabinClassPref, numberOfPassengers);

            } catch (FlightNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (FlightScheduleNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
            String currentDepartAirportCode = destinationAirportCode;
            String currentDestAirportCode = originAirportCode;
            try {
                List<Pair<FlightSchedule, FlightSchedule>> dateOfActualInbound = flightScheduleSessionBeanRemote.getIndirectFlightSchedules(currentDepartAirportCode, currentDestAirportCode, localDateTimeReturnDate, cabinClassPref);

                List<Pair<FlightSchedule, FlightSchedule>> oneDayBeforeActualInbound = flightScheduleSessionBeanRemote.getIndirectFlightSchedules(currentDepartAirportCode, currentDestAirportCode, localDateTimeReturnDate.minusDays(1), cabinClassPref);
                List<Pair<FlightSchedule, FlightSchedule>> twoDaysBeforeActualInbound = flightScheduleSessionBeanRemote.getIndirectFlightSchedules(currentDepartAirportCode, currentDestAirportCode, localDateTimeReturnDate.minusDays(2), cabinClassPref);
                List<Pair<FlightSchedule, FlightSchedule>> threeDaysBeforeActualInbound = flightScheduleSessionBeanRemote.getIndirectFlightSchedules(currentDepartAirportCode, currentDestAirportCode, localDateTimeReturnDate.minusDays(3), cabinClassPref);

                List<Pair<FlightSchedule, FlightSchedule>> oneDayAfterActualInbound = flightScheduleSessionBeanRemote.getIndirectFlightSchedules(currentDepartAirportCode, currentDestAirportCode, localDateTimeReturnDate.plusDays(1), cabinClassPref);
                List<Pair<FlightSchedule, FlightSchedule>> twoDaysAfterActualInbound = flightScheduleSessionBeanRemote.getIndirectFlightSchedules(currentDepartAirportCode, currentDestAirportCode, localDateTimeReturnDate.plusDays(2), cabinClassPref);
                List<Pair<FlightSchedule, FlightSchedule>> threeDaysAfterActualInbound = flightScheduleSessionBeanRemote.getIndirectFlightSchedules(currentDepartAirportCode, currentDestAirportCode, localDateTimeReturnDate.plusDays(3), cabinClassPref);
                System.out.println("                      ============= Available Inbound Flights (Connecting) ============= ");

                System.out.println("                             ============ On Desired Date =========== ");
                printFlightScheduleWithConnecting(dateOfActualInbound, cabinClassPref, numberOfPassengers);
                System.out.println("                             ============ Departing One Day before Desired Date =========== ");
                printFlightScheduleWithConnecting(oneDayBeforeActualInbound, cabinClassPref, numberOfPassengers);
                System.out.println("                             ============ Departing Two Days before Desired Date =========== ");
                printFlightScheduleWithConnecting(twoDaysBeforeActualInbound, cabinClassPref, numberOfPassengers);
                System.out.println("                             ============ Departing Three Days before Desired Date =========== ");
                printFlightScheduleWithConnecting(threeDaysBeforeActualInbound, cabinClassPref, numberOfPassengers);
                System.out.println("                             ============ Departing One Day After Desired Date =========== ");
                printFlightScheduleWithConnecting(oneDayAfterActualInbound, cabinClassPref, numberOfPassengers);
                System.out.println("                             ============ Departing Two Days After Desired Date =========== ");
                printFlightScheduleWithConnecting(twoDaysAfterActualInbound, cabinClassPref, numberOfPassengers);
                System.out.println("                             ============ Departing One Day before Desired Date =========== ");
                printFlightScheduleWithConnecting(threeDaysAfterActualInbound, cabinClassPref, numberOfPassengers);

            } catch (FlightNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (FlightScheduleNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        }
        if (response == 2 && flightTypeResponse == 2) { //return direct
            System.out.println("Enter return date> ");
            String returnDate = sc.nextLine();
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yy");
            LocalDate localDate1 = LocalDate.parse(returnDate, formatter);
            LocalDateTime localDateTimeReturnDate = localDate1.atStartOfDay(); //12am

            try {

                List<FlightSchedule> returnFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate, cabinClassPref);
                List<FlightSchedule> returnOneDayBeforeFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.minusDays(1), cabinClassPref);
                List<FlightSchedule> returnTwoDaysBeforeFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.minusDays(2), cabinClassPref);
                List<FlightSchedule> returnThreeDaysBeforeFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.minusDays(3), cabinClassPref);
                List<FlightSchedule> returnOneDayAfterFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.plusDays(1), cabinClassPref);
                List<FlightSchedule> returnTwoDaysAfterFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.plusDays(2), cabinClassPref);
                List<FlightSchedule> returnThreeDaysAfterFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.plusDays(3), cabinClassPref);

                System.out.println("                      ============= Available Direct Inbound Flights ============= ");
                System.out.println("                             ============ On Actual Date =========== ");
                printSingleFlightSchedule(returnFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 1 day before Actual Date ============ ");
                printSingleFlightSchedule(returnOneDayBeforeFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 days before Actual Date ============ ");
                printSingleFlightSchedule(returnTwoDaysBeforeFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 days before Actual Date ============ ");
                printSingleFlightSchedule(returnThreeDaysBeforeFlightSchedules, cabinClassPref, numberOfPassengers);

                System.out.println("\n                  ============ Departing 1 day after Actual Date ============ ");
                printSingleFlightSchedule(returnOneDayAfterFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 days after Actual Date ============ ");
                printSingleFlightSchedule(returnTwoDaysAfterFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 days after Actual Date ============ ");
                printSingleFlightSchedule(returnThreeDaysAfterFlightSchedules, cabinClassPref, numberOfPassengers);

            } catch (FlightNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (FlightScheduleNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        }

        System.out.println("\n");

        System.out.print("Would you like to reserve a flight? (Y/N)> ");
        String ans = sc.nextLine().trim();
        if (ans.equalsIgnoreCase("n")) {
            return;
        } else if (ans.equalsIgnoreCase("y") && !loggedIn) {
            try {
                doLogin();
                loggedIn = true;
                System.out.println("*** Welcome to Merlion Airlines ***\n");
                System.out.println("You are currently logged in as " + currentCustomer.getFirstName() + " " + currentCustomer.getLastName() + "!\n");
            } catch (InvalidLoginCredentialException ex) {
                System.out.println(ex.getMessage());
                return;
            }
        }
        Long outbound1, outbound2, inbound1, inbound2;
        if (response == 1 && flightTypeResponse == 2) { //1 way direct
            outbound2 = null;
            inbound2 = null;
            inbound1 = null;
            System.out.print("Enter flight you would like to reserve (Flight ID)> ");
            outbound1 = sc.nextLong();
            sc.nextLine();
        } else if (response == 2 && flightTypeResponse == 2) {// return direct
            outbound2 = null;
            inbound2 = null;
            System.out.print("Enter the outbound flight you would like to reserve (Flight ID)> ");
            outbound1 = sc.nextLong();
            System.out.print("Enter the inbound flight you would like to reserve (Flight ID)> ");
            inbound1 = sc.nextLong();
            sc.nextLine();
        } else if (flightTypeResponse == 1) { //no preference
            System.out.print("Select type of flight you would like to reserve (1. Direct Flight 2.Connecting Flight)> ");
            int choice = sc.nextInt();
            sc.nextLine();
            if (response == 1 && choice == 1) { //one way direct
                outbound2 = null;
                inbound2 = null;
                inbound1 = null;
                System.out.print("Enter flight you would like to reserve (Flight ID)> ");
                outbound1 = sc.nextLong();
                sc.nextLine();
            } else if (response == 1 && choice == 2) { //one way connecting
                inbound1 = null;
                inbound2 = null;
                System.out.print("Enter the first outbound flight you would like to reserve (Flight ID)> ");
                outbound1 = sc.nextLong();
                System.out.print("Enter the connecting outbound flight you would like to reserve (Flight ID)> ");
                outbound2 = sc.nextLong();
            } else if (response == 2 && choice == 1) { //return direct
                outbound2 = null;
                inbound2 = null;
                System.out.print("Enter the outbound flight you would like to reserve (Flight ID)> ");
                outbound1 = sc.nextLong();
                System.out.print("Enter the inbound flight you would like to reserve (Flight ID)> ");
                inbound1 = sc.nextLong();
                sc.nextLine();
            } else if (response == 2 && choice == 2) { //return connecting
                System.out.print("Enter the first outbound flight you would like to reserve (Flight ID)> ");
                outbound1 = sc.nextLong();
                System.out.print("Enter the connecting outbound flight you would like to reserve (Flight ID)> ");
                outbound2 = sc.nextLong();
                System.out.print("Enter the first inbound flight you would like to reserve (Flight ID)> ");
                inbound1 = sc.nextLong();
                System.out.print("Enter the connecting inbound flight you would like to reserve (Flight ID)> ");
                inbound2 = sc.nextLong();
            } else {
                System.out.println("Invalid option!");
                return;
            }
        } else {
            return;
        }
        reserveFlight(outbound1, outbound2, inbound1, inbound2, cabinClassPref, numberOfPassengers);

    }

    private void reserveFlight(Long ob1, Long ob2, Long ib1, Long ib2, String cabinClassPref, int numberOfPassengers) throws FlightScheduleNotFoundException, SeatInventoryNotFoundException {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("*** Reserve Flight ***\n");

            FlightSchedule outbound1FlightSchedule;
            CabinClass outbound1Seats;
            Fare outbound1Fare;
            FlightReservation outbound1Reservation;
            List<String> outbound1SeatSelection;

            BigDecimal pricePerPax;

            if (ob2 == null && ib1 == null && ib2 == null) { //one way direct
                outbound1FlightSchedule = flightScheduleSessionBeanRemote.retrieveFlightScheduleById(ob1);
                System.out.println("Seat Selection for outbound flight " + outbound1FlightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber());
                if (cabinClassPref.equals("A")) {
                    outbound1Seats = chooseCabinClass(outbound1FlightSchedule); //choose cabin class
                } else {
                    outbound1Seats = flightScheduleSessionBeanRemote.getCorrectCabinClass(outbound1FlightSchedule, cabinClassPref);
                }
                outbound1Fare = flightScheduleSessionBeanRemote.getSmallestFare(outbound1FlightSchedule, outbound1Seats.getCabinClassName());
                outbound1SeatSelection = getSeatBookings(outbound1FlightSchedule.getSeatInventory(),cabinClassPref,numberOfPassengers);
                
                outbound1Reservation = new FlightReservation();
                outbound1Reservation.getFareBasisCode().add(outbound1Fare.getFareBasisCode());
                outbound1Reservation.getFareAmount().add(outbound1Fare.getFareAmount());
                
                pricePerPax = outbound1Fare.getFareAmount();
                System.out.println("Price per person : $" + pricePerPax.toString() + "\nTotal Amount : $" + pricePerPax.multiply(new BigDecimal(numberOfPassengers)));
                
                System.out.print("Enter Credit Card Number> ");
                String creditCardNum = sc.nextLine().trim();
                System.out.print("Enter cvv> ");
                int cvv = sc.nextInt();
                sc.nextLine();
                 System.out.print("Enter Credit Card Expiry Date> ");
                String creditCardExpiry = sc.nextLine().trim();
                outbound1Reservation.setCcExpiryDate(creditCardExpiry);
                outbound1Reservation.setCcNum(creditCardNum);
                outbound1Reservation.setCvv(cvv);
                
                List<Passenger> passengers = obtainPassengerDetails(numberOfPassengers);
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).getSeats().add(outbound1SeatSelection.get(i));
                    passengers.get(i).setFlightReservations(outbound1Reservation);
                }
                
                outbound1Reservation.setCustomer(currentCustomer);
                outbound1Reservation.setCabinClasses(outbound1FlightSchedule.getSeatInventory().getAllCabinClasses());
                outbound1Reservation.getFlightSchedules().add(outbound1FlightSchedule);
                flightReservationSessionBeanRemote.createReservation(outbound1Reservation);
                
            }
        } catch (FlightScheduleNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

    }
    private List<Passenger> obtainPassengerDetails(int numPassenger) {

        Scanner sc = new Scanner(System.in);
        System.out.println("*** Passenger Details ***\n");
        List<Passenger> passengers = new ArrayList<>();
        for (int i = 1; i <= numPassenger; i++) {
            
            System.out.println("Enter passenger " + (i) + " first name> ");
            String firstName = sc.nextLine().trim();
            System.out.println("Enter passenger " + (i) + " last name> ");
            String lastName = sc.nextLine().trim();
            System.out.print("Enter passenger " + (i) + " passport number> ");
            String passport = sc.nextLine().trim();
            passengers.add(new Passenger(firstName,lastName, passport));
        }
        return passengers;
    }

    private List<String> getSeatBookings(SeatInventory seatInventory, String cabinClassChosenName, int numberOfPassengers) {
        Scanner sc = new Scanner(System.in);
        List<CabinClass> cabinClasses = seatInventory.getAllCabinClasses();
        List<List<String>> availableSeats = seatInventory.getAvailableSeats();
        List<List<String>> reservedSeats = seatInventory.getReservedSeats();
        List<List<String>> balanceSeats = seatInventory.getBalanceSeats();

        int id = 0;
        for (int i = 0; i < cabinClasses.size(); i++) {
            if (cabinClasses.get(i).getCabinClassName().equals(cabinClassChosenName)) { //find cabin class in seat inventory
                id = i;
            }
        }
        int numOfAvailableSeats = availableSeats.get(id).size();
        List<String> avail = availableSeats.get(id);
        int numOfReservedSeats = reservedSeats.get(id).size();
        List<String> reserved = reservedSeats.get(id);
        int numBalanceSeats = balanceSeats.get(id).size();
        List<String> balanced = balanceSeats.get(id);

        String type = "";
        if (cabinClassChosenName.equals("F")) {
            type = "First Class";
        } else if (cabinClassChosenName.equals("J")) {
            type = "Business Class";
        } else if (cabinClassChosenName.equals("W")) {
            type = "Premium Economy Class";
        } else {
            type = "Economy Class";
        }
        System.out.println(" --- " + type + " --- ");
        System.out.println(" --- Total Seats --- ");
        System.out.println("Number of available seats: " + numOfAvailableSeats);
        System.out.println("Number of reserved seats: " + numOfReservedSeats);
        System.out.println("Number of balance seats: " + numBalanceSeats);

        System.out.println(" --- Choose Available Seats --- ");

        List<String> seatSelection = new ArrayList<>();
        for (int i = 0; i < numberOfPassengers; i++) {
            System.out.println("Choose seats for passenger " + (i + 1));
            for (String currentSeat : avail) {
                System.out.println("Current seat: " + currentSeat);
                System.out.println("Do you want to choose this seat? y/n");
                String reply = "";
                reply = sc.nextLine();
                if (reply.equalsIgnoreCase("y")) {
                    seatSelection.add(currentSeat);
                    
                }
            }
        }
        seatInventorySessionBeanRemote.setReserved(seatInventory, id, seatSelection);
        return seatSelection;

    }

    private CabinClass chooseCabinClass(FlightSchedule flightSchedule) {
        Scanner sc = new Scanner(System.in);
        int i = 1;
        System.out.println(" * Available Cabin Classes * ");
        SeatInventory seatInventory = flightSchedule.getSeatInventory();
        for (CabinClass cabinClass : seatInventory.getAllCabinClasses()) {
            String cabinClassName;
            if (cabinClass.getCabinClassName().equals("F")) {
                cabinClassName = "First Class";

            } else if (cabinClass.getCabinClassName().equals("J")) {
                cabinClassName = "Business Class";
            } else if (cabinClass.getCabinClassName().equals("W")) {
                cabinClassName = "Premium Economy Class";
            } else {
                cabinClassName = "Economy Class";
            }
            System.out.println(i + ") " + cabinClassName);
            i++;
        }
        while (true) {
            System.out.print("Select desired cabin class> ");
            int choice = sc.nextInt();
            sc.nextLine();
            if (choice <= seatInventory.getAllCabinClasses().size() && choice >= 1) {
                return seatInventory.getAllCabinClasses().get(choice - 1);
            } else {
                System.out.println("Error: Please enter a valid input");
            }
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
        System.out.printf("%15s%20s%30s%30s%40s%20s%20s%20s%25s%25s\n", "Flight ID",
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (FlightSchedule flightSchedule : flightSchedules) {
            LocalDateTime arrivalTime = flightSchedule.getArrivalDateTime();

            //System.out.println(flightSchedule.getDepartureDateTime().format(formatter)  + "        " + flightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber());
            SeatInventory seatInventory = flightSchedule.getSeatInventory();
            //System.out.println(seatInventory.getAllCabinClasses().size());
            for (CabinClass cabinClass : seatInventory.getAllCabinClasses()) {
                String cabinClassType = "";
                if (cabinClass.getCabinClassName().equals("F") && (cabinPref.equals("F") || cabinPref.equals("A"))) {
                    cabinClassType = "First Class";
                } else if (cabinClass.getCabinClassName().equals("J") && (cabinPref.equals("J") || cabinPref.equals("A"))) {
                    cabinClassType = "Business Class";
                } else if (cabinClass.getCabinClassName().equals("W") && (cabinPref.equals("W") || cabinPref.equals("A"))) {
                    cabinClassType = "Premium Economy Class";
                } else if (cabinClass.getCabinClassName().equals("Y") && (cabinPref.equals("Y") || cabinPref.equals("A"))) {
                    cabinClassType = "Economy Class";
                } else {
                    continue;
                }
                //System.out.println(cabinClassType);
                System.out.printf("%15s%20s%30s%30s%40s%20s%20s%20s%25s%25s\n", flightSchedule.getFlightScheduleId(),
                        flightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber(),
                        flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getOriginAirport().getAirportName(),
                        flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getDestinationAirport().getAirportName(),
                        flightSchedule.getDepartureDateTime().format(formatter),
                        flightSchedule.getFlightDurationHours(),
                        arrivalTime.format(formatter),
                        cabinClassType,
                        //seats.getBalance(),
                        flightScheduleSessionBeanRemote.getSmallestFare(flightSchedule, cabinClass.getCabinClassName()).getFareAmount(),
                        flightScheduleSessionBeanRemote.getSmallestFare(flightSchedule, cabinClass.getCabinClassName()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                );

            }
        }
    }

    void printFlightScheduleWithConnecting(List<Pair<FlightSchedule, FlightSchedule>> flightSchedulePairs, String cabinPref, int passengers) throws FlightScheduleNotFoundException {
        System.out.printf("%15s%20s%40s%40s%30s%20s%30s%30s%25s%25s%25s%30s%45s%45s%40s%20s%30s%30s%25s%25s\n", "Flight ID",
                "Flight Number",
                "Departure Airport",
                "Arrival Airport",
                "Departure Date & Time",
                "Duration (HRS)",
                "Arrival Date & Time",
                "Cabin Type",
                //"Number of Seats Balanced", 
                "Price per head",
                "Total Price",
                "Connecting Flight ID",
                "Connecting Flight Number",
                "Connecting Departure Airport",
                "Arrival Airport",
                "Departure Date & Time",
                "Duration (HRS)",
                "Arrival Date & Time",
                "Cabin Type",
                //"Number of Seats Balanced", 
                "Price per head",
                "Total Price");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (Pair<FlightSchedule, FlightSchedule> pair : flightSchedulePairs) {
            FlightSchedule flightSchedule1 = pair.getKey();
            FlightSchedule flightSchedule2 = pair.getValue();
            LocalDateTime flight1arrival = flightSchedule1.getArrivalDateTime();
            LocalDateTime flight2arrival = flightSchedule2.getArrivalDateTime();
            SeatInventory seatInventory1 = flightSchedule1.getSeatInventory();
            SeatInventory seatInventory2 = flightSchedule2.getSeatInventory();
            for (CabinClass cabinClass1 : seatInventory1.getAllCabinClasses()) {
                for (CabinClass cabinClass2 : seatInventory2.getAllCabinClasses()) {
                    String cabinClassType1 = "";
                    String cabinClassType2 = "";
                    if (cabinPref.equals("A")) {
                        if (cabinClass1.getCabinClassName().equals("F")) {
                            cabinClassType1 = "First Class";
                        } else if (cabinClass1.getCabinClassName().equals("J")) {
                            cabinClassType1 = "Business Class";
                        } else if (cabinClass1.getCabinClassName().equals("W")) {
                            cabinClassType1 = "Premium Economy Class";
                        } else if (cabinClass1.getCabinClassName().equals("Y")) {
                            cabinClassType1 = "Economy Class";
                        }
                        if (cabinClass2.getCabinClassName().equals("F")) {
                            cabinClassType2 = "First Class";
                        } else if (cabinClass2.getCabinClassName().equals("J")) {
                            cabinClassType2 = "Business Class";
                        } else if (cabinClass2.getCabinClassName().equals("W")) {
                            cabinClassType2 = "Premium Economy Class";
                        } else if (cabinClass2.getCabinClassName().equals("Y")) {
                            cabinClassType2 = "Economy Class";
                        }
                    } else if (cabinClass1.getCabinClassName().equals("F") && cabinClass2.getCabinClassName().equals("F") && cabinPref.equals("F")) {
                        cabinClassType1 = "First Class";
                        cabinClassType2 = "First Class";
                    } else if (cabinClass1.getCabinClassName().equals("J") && cabinClass2.getCabinClassName().equals("J") && cabinPref.equals("J")) {
                        cabinClassType1 = "Business Class";
                        cabinClassType2 = "Business Class";
                    } else if (cabinClass1.getCabinClassName().equals("W") && cabinClass2.getCabinClassName().equals("W") && cabinPref.equals("W")) {
                        cabinClassType1 = "Premium Economy Class";
                        cabinClassType2 = "Premium Economy Class";
                    } else if (cabinClass1.getCabinClassName().equals("Y") && cabinClass2.getCabinClassName().equals("Y") && cabinPref.equals("Y")) {
                        cabinClassType1 = "Economy Class";
                        cabinClassType2 = "Economy Class";
                    } else {
                        continue;
                    }

                    System.out.printf("%15s%20s%40s%40s%30s%20s%30s%30s%25s%25s%25s%30s%45s%45s%40s%20s%30s%30s%25s%25s\n", flightSchedule1.getFlightScheduleId(),
                            flightSchedule1.getFlightSchedulePlan().getFlight().getFlightNumber(),
                            flightSchedule1.getFlightSchedulePlan().getFlight().getFlightRoute().getOriginAirport().getAirportName(),
                            flightSchedule1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestinationAirport().getAirportName(),
                            flightSchedule1.getDepartureDateTime().format(formatter),
                            flightSchedule1.getFlightDurationHours(),
                            flight1arrival.format(formatter),
                            cabinClassType1,
                            flightScheduleSessionBeanRemote.getSmallestFare(flightSchedule1, cabinClass1.getCabinClassName()).getFareAmount(),
                            flightScheduleSessionBeanRemote.getSmallestFare(flightSchedule1, cabinClass1.getCabinClassName()).getFareAmount().multiply(BigDecimal.valueOf(passengers)),
                            flightSchedule2.getFlightScheduleId(),
                            flightSchedule2.getFlightSchedulePlan().getFlight().getFlightNumber(),
                            flightSchedule2.getFlightSchedulePlan().getFlight().getFlightRoute().getOriginAirport().getAirportName(),
                            flightSchedule2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestinationAirport().getAirportName(),
                            flightSchedule2.getDepartureDateTime().format(formatter),
                            flightSchedule2.getFlightDurationHours(),
                            flight2arrival.format(formatter),
                            cabinClassType2,
                            flightScheduleSessionBeanRemote.getSmallestFare(flightSchedule2, cabinClass2.getCabinClassName()).getFareAmount(),
                            flightScheduleSessionBeanRemote.getSmallestFare(flightSchedule2, cabinClass2.getCabinClassName()).getFareAmount().multiply(BigDecimal.valueOf(passengers)));

                }
            }
        }

    }

}
