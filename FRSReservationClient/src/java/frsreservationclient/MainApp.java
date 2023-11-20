/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package frsreservationclient;

import ejb.session.stateless.AirportSessionBeanRemote;
import ejb.session.stateless.CabinClassSessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.FlightReservationSessionBeanRemote;
import ejb.session.stateless.FlightSchedulePlanSessionBeanRemote;
import ejb.session.stateless.FlightScheduleSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import ejb.session.stateless.PassengerSessionBeanRemote;
import ejb.session.stateless.SeatInventorySessionBeanRemote;
import entity.Airport;
import entity.CabinClass;
import entity.Customer;
import entity.Fare;
import entity.FlightReservation;
import entity.FlightSchedule;
import entity.Passenger;
import entity.SeatInventory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import javafx.util.Pair;
import util.exception.AirportNotFoundException;
import util.exception.CustomerAlreadyExistsException;
import util.exception.FlightNotFoundException;
import util.exception.FlightScheduleNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.NoFlightReservationsException;
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
    private CabinClassSessionBeanRemote cabinClassSessionBeanRemote;

    public MainApp() {
    }

    public MainApp(CustomerSessionBeanRemote customerSessionBeanRemote, AirportSessionBeanRemote airportSessionBeanRemote,
            FlightSessionBeanRemote flightSessionBeanRemote, FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBeanRemote,
            FlightScheduleSessionBeanRemote flightScheduleSessionBeanRemote, SeatInventorySessionBeanRemote seatInventorySessionBeanRemote,
            FlightReservationSessionBeanRemote flightReservationSessionBeanRemote, CabinClassSessionBeanRemote cabinClassSessionBeanRemote) {
        this();
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.airportSessionBeanRemote = airportSessionBeanRemote;
        this.flightSessionBeanRemote = flightSessionBeanRemote;
        this.flightSchedulePlanSessionBeanRemote = flightSchedulePlanSessionBeanRemote;
        this.flightScheduleSessionBeanRemote = flightScheduleSessionBeanRemote;
        this.seatInventorySessionBeanRemote = seatInventorySessionBeanRemote;
        this.flightReservationSessionBeanRemote = flightReservationSessionBeanRemote;
        this.cabinClassSessionBeanRemote = cabinClassSessionBeanRemote;
    }

    public void runApp() {
        Scanner sc = new Scanner(System.in);
        boolean loggedIn = false;
        Integer response = 0;
        String toContinue = "";

        while (!loggedIn) {
            System.out.println("*** Welcome to Merlion Airlines FRS Reservation ***");
            System.out.println("1: Register as customer");
            System.out.println("2: Login");
            System.out.println("3: Search Flight");
            System.out.println("4: Exit");
            System.out.print("> ");
            response = sc.nextInt();
            if (response == 1) {
                try {
                    registerAsCustomer();
                    System.out.println("Customer created successfully");
                    System.out.println();
                    loggedIn = true;
                    customerHomePage(loggedIn);
                    loggedIn = false;
                } catch (CustomerAlreadyExistsException ex) {
                    System.out.println("Customer already exists : " + ex.getMessage() + "\n");
                }
            } else if (response == 2) {
                try {
                    doLogin();
                    System.out.println("Log-in successful!");
                    System.out.println();
                    loggedIn = true;
                    customerHomePage(loggedIn);
                } catch (InvalidLoginCredentialException ex) {
                    System.out.println("Invalid login credential : " + ex.getMessage() + "\n");
                }

            } else if (response == 3) {
                try {
                    searchFlight(loggedIn);
                } catch (SeatInventoryNotFoundException ex) {
                    System.out.println(ex.getMessage());
                } catch (AirportNotFoundException ex) {
                    System.out.println(ex.getMessage());
                }
            } else if (response == 4) {
                break;
            }
        }
    }

//    void doLogOut() {
//    }

    void customerHomePage(boolean loggedIn) {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        while (loggedIn) {
            System.out.println("Hello, " + currentCustomer.getFirstName() + "!");
            System.out.println("What would you like to do today?");
            System.out.println("1: Reserve Flight");
            System.out.println("2: View My Flight Reservations");
            System.out.println("3: View My Flight Reservation Details");
            System.out.println("4: Log Out");
            response = 0;
            while (response < 1 || response > 4) {
                System.out.print("> ");
                response = sc.nextInt();

                if (response == 1) {
                    try {
                        searchFlight(loggedIn);
                    } catch (SeatInventoryNotFoundException ex) {
                        System.out.println(ex.getMessage());
                    } catch (AirportNotFoundException ex) {
                        System.out.println(ex.getMessage());
                    }
                } else if (response == 2) {
                    viewFlightReservation();
                } else if (response == 3) {
                    viewFlightReservationDetails();
                } else if (response == 4) {
                    System.out.println("Log out successful.\n");
                    loggedIn = false;
                    break;
                } else {
                    System.out.println("Invalid Option, please try again!");
                }
            }
            if (response == 4) {
                break;
            }
        }
    }

    void searchFlight(boolean loggedIn) throws SeatInventoryNotFoundException, AirportNotFoundException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yy");

        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        String originAirportCode = "";
        String destinationAirportCode = "";

        System.out.println("** Search for flight **");
        System.out.println("1: One-way flight");
        System.out.println("2: Round-trip");
        System.out.print("> ");
        response = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter departure airport IATA code > ");
        originAirportCode = sc.nextLine().trim();
        Airport originAirport;
        originAirport = airportSessionBeanRemote.retrieveAirportByIATACode(originAirportCode);

        System.out.print("Enter destination airport IATA code > ");
        destinationAirportCode = sc.nextLine().trim();
        Airport destinationAirport = airportSessionBeanRemote.retrieveAirportByIATACode(destinationAirportCode);
        System.out.print("Enter departure date (d MMM yy) (eg. 1 Jan 23) > ");
        String departDate = sc.nextLine();
        LocalDate localDate = LocalDate.parse(departDate, formatter);
        LocalDateTime localDateTimeDepartDate = localDate.atStartOfDay(); //12am
        LocalDate localReturnDate = LocalDate.parse(departDate, formatter);;
        LocalDateTime localDateTimeReturnDate = localDate.atStartOfDay(); 
        if (response == 2) {
            System.out.print("Enter return date (d MMM yy) (eg. 1 Jan 23) > ");
            String returnDate = sc.nextLine();
            localReturnDate = LocalDate.parse(returnDate, formatter);
            localDateTimeReturnDate = localReturnDate.atStartOfDay();

        }
        System.out.print("Enter number of passsengers > ");
        Integer numberOfPassengers = sc.nextInt();

        System.out.println("Select preference for flight: ");
        System.out.println("1: No Preference "); //show direct and connecting
        System.out.println("2: Direct Flight ");
        System.out.print("> ");
        Integer flightTypeResponse = 0;
        flightTypeResponse = sc.nextInt();
        sc.nextLine();

        String cabinClassPref = "";
        System.out.println("Select preference for cabin class: ");
        System.out.println("A = No preference");
        System.out.println("F = First class");
        System.out.println("J = Business class");
        System.out.println("W = Premium economy");
        System.out.println("Y = Economy");
        System.out.print("> ");
        cabinClassPref = sc.nextLine();

        if (flightTypeResponse == 1 && response == 2) {

            try {
                List<FlightSchedule> flightSchedulesFound = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate, cabinClassPref);
                List<FlightSchedule> flightSchedulesAddOneDay = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.plusDays(1), cabinClassPref);
                List<FlightSchedule> flightSchedulesAddTwoDays = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.plusDays(2), cabinClassPref);
                List<FlightSchedule> flightSchedulesAddThreeDays = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.plusDays(3), cabinClassPref);

                List<FlightSchedule> flightSchedulesMinusOneDay = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.minusDays(1), cabinClassPref);
                List<FlightSchedule> flightSchedulesMinusTwoDays = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.minusDays(2), cabinClassPref);
                List<FlightSchedule> flightSchedulesMinusThreeDays = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.minusDays(3), cabinClassPref);

                System.out.println();
                System.out.println("                      ============= Available Direct Outbound Flights ============= ");
                System.out.println();
                System.out.println("                             ============ On Actual Date =========== ");
                printSingleFlightSchedule(flightSchedulesFound, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 1 Day Before Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesMinusOneDay, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 Days Before Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesMinusTwoDays, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 Days Before Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesMinusThreeDays, cabinClassPref, numberOfPassengers);
                System.out.println();
                System.out.println("\n                  ============ Departing 1 Day After Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesAddOneDay, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 Days After Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesAddTwoDays, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 Days After Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesAddThreeDays, cabinClassPref, numberOfPassengers);
                System.out.println();
            } catch (FlightNotFoundException ex) {
                System.out.print("Sorry, there are no flights with your desired flight route!\n");
            } catch (FlightScheduleNotFoundException ex) {
                System.out.println(ex.getMessage());
            }

            String departAirportCode = originAirportCode;
            String destination = destinationAirportCode;

            try {
                List<Pair<FlightSchedule, FlightSchedule>> dateOfActualOutbound = flightScheduleSessionBeanRemote.getIndirectFlightSchedules(departAirportCode, destinationAirportCode, localDateTimeDepartDate, localDateTimeReturnDate, cabinClassPref);
                List<Pair<FlightSchedule, FlightSchedule>> oneDayAfterActualOutbound = flightScheduleSessionBeanRemote.getIndirectFlightSchedules(departAirportCode, destinationAirportCode, localDateTimeDepartDate.plusDays(1), localDateTimeReturnDate, cabinClassPref);
                List<Pair<FlightSchedule, FlightSchedule>> twoDaysAfterActualOutbound = flightScheduleSessionBeanRemote.getIndirectFlightSchedules(departAirportCode, destinationAirportCode, localDateTimeDepartDate.plusDays(2), localDateTimeReturnDate, cabinClassPref);
                List<Pair<FlightSchedule, FlightSchedule>> threeDaysAfterActualOutbound = flightScheduleSessionBeanRemote.getIndirectFlightSchedules(departAirportCode, destinationAirportCode, localDateTimeDepartDate.plusDays(3), localDateTimeReturnDate, cabinClassPref);

                List<Pair<FlightSchedule, FlightSchedule>> oneDayBeforeActualOutbound = flightScheduleSessionBeanRemote.getIndirectFlightSchedules(departAirportCode, destinationAirportCode, localDateTimeDepartDate.minusDays(1), localDateTimeReturnDate, cabinClassPref);
                List<Pair<FlightSchedule, FlightSchedule>> twoDaysBeforeActualOutbound = flightScheduleSessionBeanRemote.getIndirectFlightSchedules(departAirportCode, destinationAirportCode, localDateTimeDepartDate.minusDays(2), localDateTimeReturnDate, cabinClassPref);
                List<Pair<FlightSchedule, FlightSchedule>> threeDaysBeforeActualOutbound = flightScheduleSessionBeanRemote.getIndirectFlightSchedules(departAirportCode, destinationAirportCode, localDateTimeDepartDate.minusDays(3), localDateTimeReturnDate, cabinClassPref);

                System.out.println();
                System.out.println("                      ============= Available Outbound Flights ============= ");
                System.out.println();
                System.out.println("                             ============ On Desired Date =========== ");
                printFlightScheduleWithConnecting(dateOfActualOutbound, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 1 Day Before Actual Date ============ ");
                printFlightScheduleWithConnecting(oneDayBeforeActualOutbound, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 Days Before Actual Date ============ ");
                printFlightScheduleWithConnecting(twoDaysBeforeActualOutbound, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 Days Before Actual Date ============ ");
                printFlightScheduleWithConnecting(threeDaysBeforeActualOutbound, cabinClassPref, numberOfPassengers);
                System.out.println();
                System.out.println("\n                  ============ Departing 1 Day After Actual Date ============ ");
                printFlightScheduleWithConnecting(oneDayAfterActualOutbound, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 Days After Actual Date ============ ");
                printFlightScheduleWithConnecting(twoDaysAfterActualOutbound, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 Days After Actual Date ============ ");
                printFlightScheduleWithConnecting(threeDaysAfterActualOutbound, cabinClassPref, numberOfPassengers);
                System.out.println();
            } catch (FlightNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (FlightScheduleNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        }
        
        if (flightTypeResponse == 1 && response == 1) {

            try {
                List<FlightSchedule> flightSchedulesFound = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate, cabinClassPref);
                List<FlightSchedule> flightSchedulesAddOneDay = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.plusDays(1), cabinClassPref);
                List<FlightSchedule> flightSchedulesAddTwoDays = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.plusDays(2), cabinClassPref);
                List<FlightSchedule> flightSchedulesAddThreeDays = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.plusDays(3), cabinClassPref);

                List<FlightSchedule> flightSchedulesMinusOneDay = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.minusDays(1), cabinClassPref);
                List<FlightSchedule> flightSchedulesMinusTwoDays = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.minusDays(2), cabinClassPref);
                List<FlightSchedule> flightSchedulesMinusThreeDays = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.minusDays(3), cabinClassPref);

                
                System.out.println("                      ============= Available Direct Outbound Flights ============= ");
                System.out.println();
                System.out.println("                             ============ On Actual Date =========== ");
                printSingleFlightSchedule(flightSchedulesFound, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 1 Day Before Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesMinusOneDay, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 Days Before Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesMinusTwoDays, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 Days Before Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesMinusThreeDays, cabinClassPref, numberOfPassengers);
                System.out.println();
                System.out.println("\n                  ============ Departing 1 Day After Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesAddOneDay, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 Days After Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesAddTwoDays, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 Days After Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesAddThreeDays, cabinClassPref, numberOfPassengers);
                System.out.println();
            } catch (FlightNotFoundException ex) {
                System.out.print("Sorry, there are no flights with your desired flight route!\n");
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

                System.out.println();
                System.out.println("                      ============= Available Outbound Flights ============= ");
                System.out.println();
                System.out.println("                             ============ On Desired Date =========== ");
                printFlightScheduleWithConnecting(dateOfActualOutbound, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 1 Day Before Actual Date ============ ");
                printFlightScheduleWithConnecting(oneDayBeforeActualOutbound, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 Days Before Actual Date ============ ");
                printFlightScheduleWithConnecting(twoDaysBeforeActualOutbound, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 Days Before Actual Date ============ ");
                printFlightScheduleWithConnecting(threeDaysBeforeActualOutbound, cabinClassPref, numberOfPassengers);
                System.out.println();
                System.out.println("\n                  ============ Departing 1 Day After Actual Date ============ ");
                printFlightScheduleWithConnecting(oneDayAfterActualOutbound, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 Days After Actual Date ============ ");
                printFlightScheduleWithConnecting(twoDaysAfterActualOutbound, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 Days After Actual Date ============ ");
                printFlightScheduleWithConnecting(threeDaysAfterActualOutbound, cabinClassPref, numberOfPassengers);
                System.out.println();
            } catch (FlightNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (FlightScheduleNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        }

        if (flightTypeResponse == 2) {
            String departAirportCode = originAirportCode;
            String destination = destinationAirportCode;

            try {
                List<FlightSchedule> flightSchedulesFound = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate, cabinClassPref);
                List<FlightSchedule> flightSchedulesAddOneDay = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.plusDays(1), cabinClassPref);
                List<FlightSchedule> flightSchedulesAddTwoDays = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.plusDays(2), cabinClassPref);
                List<FlightSchedule> flightSchedulesAddThreeDays = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.plusDays(3), cabinClassPref);

                List<FlightSchedule> flightSchedulesMinusOneDay = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.minusDays(1), cabinClassPref);
                List<FlightSchedule> flightSchedulesMinusTwoDays = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.minusDays(2), cabinClassPref);
                List<FlightSchedule> flightSchedulesMinusThreeDays = flightScheduleSessionBeanRemote.getFlightSchedules(originAirport, destinationAirport, localDateTimeDepartDate.minusDays(3), cabinClassPref);

                System.out.println();
                System.out.println("                      ============= Available Direct Outbound Flights ============= ");
                System.out.println();
                System.out.println("                             ============ On Actual Date =========== ");
                printSingleFlightSchedule(flightSchedulesFound, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 1 Day Before Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesMinusOneDay, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 Days Before Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesMinusTwoDays, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 Days Before Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesMinusThreeDays, cabinClassPref, numberOfPassengers);
                System.out.println();
                System.out.println("\n                  ============ Departing 1 Day After Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesAddOneDay, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 Days After Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesAddTwoDays, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 Days After Actual Date ============ ");
                printSingleFlightSchedule(flightSchedulesAddThreeDays, cabinClassPref, numberOfPassengers);
                System.out.println();
            } catch (FlightNotFoundException ex) {
                System.out.print("Sorry, there are no flights with your desired flight route!\n");
            } catch (FlightScheduleNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        }

        if (response == 2 && flightTypeResponse == 1) {//return direct/connecting

            try {

                List<FlightSchedule> returnFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate, cabinClassPref);
                List<FlightSchedule> returnOneDayBeforeFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.minusDays(1), cabinClassPref);
                List<FlightSchedule> returnTwoDaysBeforeFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.minusDays(2), cabinClassPref);
                List<FlightSchedule> returnThreeDaysBeforeFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.minusDays(3), cabinClassPref);
                List<FlightSchedule> returnOneDayAfterFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.plusDays(1), cabinClassPref);
                List<FlightSchedule> returnTwoDaysAfterFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.plusDays(2), cabinClassPref);
                List<FlightSchedule> returnThreeDaysAfterFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.plusDays(3), cabinClassPref);

                System.out.println();
                System.out.println("                      ============= Available Direct Inbound Flights ============= ");
                System.out.println();
                System.out.println("                             ============ On Actual Date =========== ");
                printSingleFlightSchedule(returnFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 1 Day Before Actual Date ============ ");
                printSingleFlightSchedule(returnOneDayBeforeFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 Days Bfore Actual Date ============ ");
                printSingleFlightSchedule(returnTwoDaysBeforeFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 Days Before Actual Date ============ ");
                printSingleFlightSchedule(returnThreeDaysBeforeFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println();
                System.out.println("\n                  ============ Departing 1 Day After Actual Date ============ ");
                printSingleFlightSchedule(returnOneDayAfterFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 Days After Actual Date ============ ");
                printSingleFlightSchedule(returnTwoDaysAfterFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 Days After Actual Date ============ ");
                printSingleFlightSchedule(returnThreeDaysAfterFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println();
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
                
                System.out.println();
                System.out.println("                      ============= Available Inbound Flights (Connecting) ============= ");
                System.out.println();   
                System.out.println("                             ============ On Desired Date =========== ");
                printFlightScheduleWithConnecting(dateOfActualInbound, cabinClassPref, numberOfPassengers);
                System.out.println("                             ============ Departing One Day Before Desired Date =========== ");
                printFlightScheduleWithConnecting(oneDayBeforeActualInbound, cabinClassPref, numberOfPassengers);
                System.out.println("                             ============ Departing Two Days Before Desired Date =========== ");
                printFlightScheduleWithConnecting(twoDaysBeforeActualInbound, cabinClassPref, numberOfPassengers);
                System.out.println("                             ============ Departing Three Days Before Desired Date =========== ");
                printFlightScheduleWithConnecting(threeDaysBeforeActualInbound, cabinClassPref, numberOfPassengers);
                System.out.println();
                System.out.println("                             ============ Departing One Day After Desired Date =========== ");
                printFlightScheduleWithConnecting(oneDayAfterActualInbound, cabinClassPref, numberOfPassengers);
                System.out.println("                             ============ Departing Two Days After Desired Date =========== ");
                printFlightScheduleWithConnecting(twoDaysAfterActualInbound, cabinClassPref, numberOfPassengers);
                System.out.println("                             ============ Departing Three Days After Desired Date =========== ");
                printFlightScheduleWithConnecting(threeDaysAfterActualInbound, cabinClassPref, numberOfPassengers);
                System.out.println();
            } catch (FlightNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (FlightScheduleNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        }

        if (response == 2 && flightTypeResponse == 2) { //return direct

            try {

                List<FlightSchedule> returnFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate, cabinClassPref);
                List<FlightSchedule> returnOneDayBeforeFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.minusDays(1), cabinClassPref);
                List<FlightSchedule> returnTwoDaysBeforeFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.minusDays(2), cabinClassPref);
                List<FlightSchedule> returnThreeDaysBeforeFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.minusDays(3), cabinClassPref);
                List<FlightSchedule> returnOneDayAfterFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.plusDays(1), cabinClassPref);
                List<FlightSchedule> returnTwoDaysAfterFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.plusDays(2), cabinClassPref);
                List<FlightSchedule> returnThreeDaysAfterFlightSchedules = flightScheduleSessionBeanRemote.getFlightSchedules(destinationAirport, originAirport, localDateTimeReturnDate.plusDays(3), cabinClassPref);

                System.out.println();
                System.out.println("                      ============= Available Direct Inbound Flights ============= ");
                System.out.println();
                System.out.println("                             ============ On Actual Date =========== ");
                printSingleFlightSchedule(returnFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 1 Day Before Actual Date ============ ");
                printSingleFlightSchedule(returnOneDayBeforeFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 Days Before Actual Date ============ ");
                printSingleFlightSchedule(returnTwoDaysBeforeFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 Days Before Actual Date ============ ");
                printSingleFlightSchedule(returnThreeDaysBeforeFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println();
                System.out.println("\n                  ============ Departing 1 Day After Actual Date ============ ");
                printSingleFlightSchedule(returnOneDayAfterFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 2 Days After Actual Date ============ ");
                printSingleFlightSchedule(returnTwoDaysAfterFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println("\n                  ============ Departing 3 Days After Actual Date ============ ");
                printSingleFlightSchedule(returnThreeDaysAfterFlightSchedules, cabinClassPref, numberOfPassengers);
                System.out.println();
            } catch (FlightNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (FlightScheduleNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        }

        System.out.println("\n");

        System.out.print("Would you like to reserve a flight? (Y/N) > ");
        String ans = sc.nextLine().trim();
        if (ans.equalsIgnoreCase("n")) {
            return;
        } else if (ans.equalsIgnoreCase("y") && !loggedIn) {
            try {
                doLogin();
                //sc.nextLine()
                loggedIn = true;
                System.out.println("Hello, " + currentCustomer.getFirstName() + "!\n");
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
            System.out.print("Enter the Flight Schedule ID for your reservation > ");
            outbound1 = sc.nextLong();
            sc.nextLine();
        } else if (response == 2 && flightTypeResponse == 2) {// return direct
            outbound2 = null;
            inbound2 = null;
            System.out.print("Enter the outbound Flight Schedule ID for your reservation > ");
            outbound1 = sc.nextLong();
            System.out.print("Enter the inbound Flight Schedule ID for your reservation > ");
            inbound1 = sc.nextLong();
            sc.nextLine();
        } else if (flightTypeResponse == 1) { //no preference
            System.out.print("Select type of flight you would like to reserve (1. Direct Flight 2.Connecting Flight) > ");
            int choice = sc.nextInt();
            sc.nextLine();
            if (response == 1 && choice == 1) { //one way direct
                outbound2 = null;
                inbound2 = null;
                inbound1 = null;
                System.out.print("Enter the Flight Schedule ID for your reservation > ");
                outbound1 = sc.nextLong();
                sc.nextLine();
            } else if (response == 1 && choice == 2) { //one way connecting
                inbound1 = null;
                inbound2 = null;
                System.out.print("Enter the outbound Flight Schedule ID for your reservation > ");
                outbound1 = sc.nextLong();
                System.out.print("Enter the inbound Flight Schedule ID for your reservation > ");
                outbound2 = sc.nextLong();
            } else if (response == 2 && choice == 1) { //return direct
                outbound2 = null;
                inbound2 = null;
                System.out.print("Enter the outbound Flight Schedule ID for your reservation > ");
                outbound1 = sc.nextLong();
                System.out.print("Enter the inbound Flight Schedule ID for your reservation > ");
                inbound1 = sc.nextLong();
                sc.nextLine();
            } else if (response == 2 && choice == 2) { //return connecting
                System.out.print("Enter the first outbound Flight Schedule ID for your reservation > ");
                outbound1 = sc.nextLong();
                System.out.print("Enter the connecting outbound Flight Schedule ID for your reservation > ");
                outbound2 = sc.nextLong();
                System.out.print("Enter the first inbound Flight Schedule ID for your reservation > ");
                inbound1 = sc.nextLong();
                System.out.print("Enter the connecting inbound Flight Schedule ID for your reservation > ");
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

    private void reserveFlight(Long ob1, Long ob2, Long ib1, Long ib2, String cabinClassPref, int numberOfPassengers) throws SeatInventoryNotFoundException {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("*** Reserve Flight ***\n");

            FlightSchedule outbound1FlightSchedule;
            CabinClass outbound1Seats;
            Fare outbound1Fare;
            FlightReservation outbound1Reservation;
            List<String> outbound1SeatSelection;

            FlightSchedule outbound2FlightSchedule;
            CabinClass outbound2Seats;
            Fare outbound2Fare;
            FlightReservation outbound2Reservation;
            List<String> outbound2SeatSelection;

            FlightSchedule inbound1FlightSchedule;
            CabinClass inbound1Seats;
            Fare inbound1Fare;
            FlightReservation inbound1Reservation;
            List<String> inbound1SeatSelection;

            FlightSchedule inbound2FlightSchedule;
            CabinClass inbound2Seats;
            Fare inbound2Fare;
            FlightReservation inbound2Reservation;
            List<String> inbound2SeatSelection;

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
                Pair<List<String>, Integer> outbound1pair = getSeatBookings(outbound1FlightSchedule.getSeatInventory(), outbound1Seats.getCabinClassName(), numberOfPassengers);
                outbound1SeatSelection = outbound1pair.getKey();
                Integer cabinClassId = outbound1pair.getValue();

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
                outbound1Reservation.setPassengers(passengers);

                for (CabinClass cabinClass : outbound1FlightSchedule.getSeatInventory().getAllCabinClasses()) {
                    outbound1Reservation.getCabinClassIds().add(cabinClass.getCabinClassId());
                }

                outbound1Reservation.getFlightSchedules().add(outbound1FlightSchedule);

                seatInventorySessionBeanRemote.setReserved(outbound1FlightSchedule.getSeatInventory(), cabinClassId, outbound1SeatSelection);
                outbound1Reservation = flightReservationSessionBeanRemote.createReservation(outbound1Reservation);

                System.out.println("Flight reservation " + outbound1Reservation.getFlightReservationId() + " created successfully");

            } else if (ob2 == null && ib2 == null) { //return direct
                outbound1FlightSchedule = flightScheduleSessionBeanRemote.retrieveFlightScheduleById(ob1);

                System.out.println("Seat Selection for outbound flight " + outbound1FlightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber());
                if (cabinClassPref.equals("A")) {
                    outbound1Seats = chooseCabinClass(outbound1FlightSchedule); //choose cabin class
                } else {
                    outbound1Seats = flightScheduleSessionBeanRemote.getCorrectCabinClass(outbound1FlightSchedule, cabinClassPref);
                }
                outbound1Fare = flightScheduleSessionBeanRemote.getSmallestFare(outbound1FlightSchedule, outbound1Seats.getCabinClassName());
                Pair<List<String>, Integer> outbound1pair = getSeatBookings(outbound1FlightSchedule.getSeatInventory(), outbound1Seats.getCabinClassName(), numberOfPassengers);
                outbound1SeatSelection = outbound1pair.getKey();
                Integer outbound1Id = outbound1pair.getValue();

                outbound1Reservation = new FlightReservation();
                outbound1Reservation.getFareBasisCode().add(outbound1Fare.getFareBasisCode());
                outbound1Reservation.getFareAmount().add(outbound1Fare.getFareAmount());

                inbound1FlightSchedule = flightScheduleSessionBeanRemote.retrieveFlightScheduleById(ib1);
                System.out.println("Seat Selection for inbound flight " + inbound1FlightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber());
                if (cabinClassPref.equals("A")) {
                    inbound1Seats = chooseCabinClass(inbound1FlightSchedule); //choose cabin class
                } else {
                    inbound1Seats = flightScheduleSessionBeanRemote.getCorrectCabinClass(inbound1FlightSchedule, cabinClassPref);
                }
                inbound1Fare = flightScheduleSessionBeanRemote.getSmallestFare(inbound1FlightSchedule, inbound1Seats.getCabinClassName());
                Pair<List<String>, Integer> inbound1pair = getSeatBookings(inbound1FlightSchedule.getSeatInventory(), inbound1Seats.getCabinClassName(), numberOfPassengers);
                inbound1SeatSelection = inbound1pair.getKey();
                Integer inbound1Id = inbound1pair.getValue();

                //inbound1Reservation = new FlightReservation();
                outbound1Reservation.getFareBasisCode().add(inbound1Fare.getFareBasisCode());
                outbound1Reservation.getFareAmount().add(inbound1Fare.getFareAmount());

                pricePerPax = outbound1Fare.getFareAmount().add(inbound1Fare.getFareAmount());
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
                outbound1Reservation.setCustomer(currentCustomer);

                List<Passenger> passengers = obtainPassengerDetails(numberOfPassengers);
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).getSeats().add(outbound1SeatSelection.get(i));
                    passengers.get(i).setFlightReservations(outbound1Reservation);
                }
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).getSeats().add(inbound1SeatSelection.get(i));
                }
                outbound1Reservation.setPassengers(passengers);

                for (CabinClass cabinClass : outbound1FlightSchedule.getSeatInventory().getAllCabinClasses()) {
                    outbound1Reservation.getCabinClassIds().add(cabinClass.getCabinClassId());
                }
                for (CabinClass cabinClass2 : inbound1FlightSchedule.getSeatInventory().getAllCabinClasses()) {
                    outbound1Reservation.getCabinClassIds().add(cabinClass2.getCabinClassId());
                }
                outbound1Reservation.getFlightSchedules().add(outbound1FlightSchedule);
                outbound1Reservation.getFlightSchedules().add(inbound1FlightSchedule);

                seatInventorySessionBeanRemote.setReserved(outbound1FlightSchedule.getSeatInventory(), outbound1Id, outbound1SeatSelection);
                seatInventorySessionBeanRemote.setReserved(inbound1FlightSchedule.getSeatInventory(), inbound1Id, inbound1SeatSelection);
                outbound1Reservation = flightReservationSessionBeanRemote.createReservation(outbound1Reservation);

                System.out.println("Flight reservation " + outbound1Reservation.getFlightReservationId() + " created successfully");

            } else if (ib1 == null && ib2 == null) { //one-way connecting
                outbound1FlightSchedule = flightScheduleSessionBeanRemote.retrieveFlightScheduleById(ob1);

                System.out.println("Seat Selection for outbound flight " + outbound1FlightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber());
                if (cabinClassPref.equals("A")) {
                    outbound1Seats = chooseCabinClass(outbound1FlightSchedule); //choose cabin class
                } else {
                    outbound1Seats = flightScheduleSessionBeanRemote.getCorrectCabinClass(outbound1FlightSchedule, cabinClassPref);
                }
                outbound1Fare = flightScheduleSessionBeanRemote.getSmallestFare(outbound1FlightSchedule, outbound1Seats.getCabinClassName());
                Pair<List<String>, Integer> outbound1pair = getSeatBookings(outbound1FlightSchedule.getSeatInventory(), outbound1Seats.getCabinClassName(), numberOfPassengers);
                outbound1SeatSelection = outbound1pair.getKey();
                Integer outbound1Id = outbound1pair.getValue();

                outbound1Reservation = new FlightReservation();
                outbound1Reservation.getFareBasisCode().add(outbound1Fare.getFareBasisCode());
                outbound1Reservation.getFareAmount().add(outbound1Fare.getFareAmount());

                outbound2FlightSchedule = flightScheduleSessionBeanRemote.retrieveFlightScheduleById(ob2);
                System.out.println("Seat Selection for outbound flight " + outbound2FlightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber());
                if (cabinClassPref.equals("A")) {
                    outbound2Seats = chooseCabinClass(outbound2FlightSchedule); //choose cabin class
                } else {
                    outbound2Seats = flightScheduleSessionBeanRemote.getCorrectCabinClass(outbound2FlightSchedule, cabinClassPref);
                }
                outbound2Fare = flightScheduleSessionBeanRemote.getSmallestFare(outbound2FlightSchedule, outbound2Seats.getCabinClassName());
                Pair<List<String>, Integer> outbound2pair = getSeatBookings(outbound2FlightSchedule.getSeatInventory(), outbound2Seats.getCabinClassName(), numberOfPassengers);
                outbound2SeatSelection = outbound2pair.getKey();
                Integer outbound2Id = outbound2pair.getValue();

                outbound1Reservation.getFareBasisCode().add(outbound2Fare.getFareBasisCode());
                outbound1Reservation.getFareAmount().add(outbound2Fare.getFareAmount());

                pricePerPax = outbound1Fare.getFareAmount().add(outbound2Fare.getFareAmount());
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
                outbound1Reservation.setCustomer(currentCustomer);

                List<Passenger> passengers = obtainPassengerDetails(numberOfPassengers);
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).getSeats().add(outbound1SeatSelection.get(i));
                    passengers.get(i).setFlightReservations(outbound1Reservation);
                }
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).getSeats().add(outbound2SeatSelection.get(i));
                }
                outbound1Reservation.setPassengers(passengers);
                outbound1Reservation.getFlightSchedules().add(outbound1FlightSchedule);
                outbound1Reservation.getFlightSchedules().add(outbound2FlightSchedule);
                for (CabinClass cabinClass : outbound1FlightSchedule.getSeatInventory().getAllCabinClasses()) {
                    outbound1Reservation.getCabinClassIds().add(cabinClass.getCabinClassId());
                }
                for (CabinClass cabinClass : outbound2FlightSchedule.getSeatInventory().getAllCabinClasses()) {
                    outbound1Reservation.getCabinClassIds().add(cabinClass.getCabinClassId());
                }

                seatInventorySessionBeanRemote.setReserved(outbound1FlightSchedule.getSeatInventory(), outbound1Id, outbound1SeatSelection);
                seatInventorySessionBeanRemote.setReserved(outbound2FlightSchedule.getSeatInventory(), outbound2Id, outbound2SeatSelection);
                outbound1Reservation = flightReservationSessionBeanRemote.createReservation(outbound1Reservation);
                System.out.println("Flight reservation " + outbound1Reservation.getFlightReservationId() + " created successfully");

            } else { //return connecting
                outbound1FlightSchedule = flightScheduleSessionBeanRemote.retrieveFlightScheduleById(ob1);

                System.out.println("Seat Selection for outbound flight " + outbound1FlightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber());
                if (cabinClassPref.equals("A")) {
                    outbound1Seats = chooseCabinClass(outbound1FlightSchedule); //choose cabin class
                } else {
                    outbound1Seats = flightScheduleSessionBeanRemote.getCorrectCabinClass(outbound1FlightSchedule, cabinClassPref);
                }
                outbound1Fare = flightScheduleSessionBeanRemote.getSmallestFare(outbound1FlightSchedule, outbound1Seats.getCabinClassName());
                Pair<List<String>, Integer> outbound1pair = getSeatBookings(outbound1FlightSchedule.getSeatInventory(), outbound1Seats.getCabinClassName(), numberOfPassengers);
                outbound1SeatSelection = outbound1pair.getKey();
                Integer outbound1Id = outbound1pair.getValue();

                outbound1Reservation = new FlightReservation();
                outbound1Reservation.getFareBasisCode().add(outbound1Fare.getFareBasisCode());
                outbound1Reservation.getFareAmount().add(outbound1Fare.getFareAmount());

                outbound2FlightSchedule = flightScheduleSessionBeanRemote.retrieveFlightScheduleById(ob2);
                System.out.println("Seat Selection for outbound flight " + outbound2FlightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber());
                if (cabinClassPref.equals("A")) {
                    outbound2Seats = chooseCabinClass(outbound2FlightSchedule); //choose cabin class
                } else {
                    outbound2Seats = flightScheduleSessionBeanRemote.getCorrectCabinClass(outbound2FlightSchedule, cabinClassPref);
                }
                outbound2Fare = flightScheduleSessionBeanRemote.getSmallestFare(outbound2FlightSchedule, outbound2Seats.getCabinClassName());
                Pair<List<String>, Integer> outbound2pair = getSeatBookings(outbound2FlightSchedule.getSeatInventory(), outbound2Seats.getCabinClassName(), numberOfPassengers);
                outbound2SeatSelection = outbound2pair.getKey();
                Integer outbound2Id = outbound2pair.getValue();

                outbound1Reservation.getFareBasisCode().add(outbound2Fare.getFareBasisCode());
                outbound1Reservation.getFareAmount().add(outbound2Fare.getFareAmount());

                inbound1FlightSchedule = flightScheduleSessionBeanRemote.retrieveFlightScheduleById(ib1);
                System.out.println("Seat Selection for inbound flight " + inbound1FlightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber());
                if (cabinClassPref.equals("A")) {
                    inbound1Seats = chooseCabinClass(inbound1FlightSchedule); //choose cabin class
                } else {
                    inbound1Seats = flightScheduleSessionBeanRemote.getCorrectCabinClass(inbound1FlightSchedule, cabinClassPref);
                }
                inbound1Fare = flightScheduleSessionBeanRemote.getSmallestFare(inbound1FlightSchedule, inbound1Seats.getCabinClassName());
                Pair<List<String>, Integer> inbound1pair = getSeatBookings(inbound1FlightSchedule.getSeatInventory(), inbound1Seats.getCabinClassName(), numberOfPassengers);
                inbound1SeatSelection = inbound1pair.getKey();
                Integer inbound1Id = inbound1pair.getValue();

                outbound1Reservation.getFareBasisCode().add(inbound1Fare.getFareBasisCode());
                outbound1Reservation.getFareAmount().add(inbound1Fare.getFareAmount());

                inbound2FlightSchedule = flightScheduleSessionBeanRemote.retrieveFlightScheduleById(ib2);
                System.out.println("Seat Selection for inbound flight " + inbound2FlightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber());
                if (cabinClassPref.equals("A")) {
                    inbound2Seats = chooseCabinClass(inbound2FlightSchedule); //choose cabin class
                } else {
                    inbound2Seats = flightScheduleSessionBeanRemote.getCorrectCabinClass(inbound2FlightSchedule, cabinClassPref);
                }
                inbound2Fare = flightScheduleSessionBeanRemote.getSmallestFare(inbound2FlightSchedule, inbound2Seats.getCabinClassName());
                Pair<List<String>, Integer> inbound2pair = getSeatBookings(inbound2FlightSchedule.getSeatInventory(), inbound2Seats.getCabinClassName(), numberOfPassengers);
                inbound2SeatSelection = inbound2pair.getKey();
                Integer inbound2Id = inbound2pair.getValue();

                outbound1Reservation.getFareBasisCode().add(inbound2Fare.getFareBasisCode());
                outbound1Reservation.getFareAmount().add(inbound2Fare.getFareAmount());

                pricePerPax = outbound1Fare.getFareAmount().add(outbound2Fare.getFareAmount()).add(inbound1Fare.getFareAmount()).add(inbound2Fare.getFareAmount());
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
                outbound1Reservation.setCustomer(currentCustomer);

                List<Passenger> passengers = obtainPassengerDetails(numberOfPassengers);
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).getSeats().add(outbound1SeatSelection.get(i));
                    passengers.get(i).setFlightReservations(outbound1Reservation);
                }
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).getSeats().add(outbound2SeatSelection.get(i));
                }
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).getSeats().add(inbound1SeatSelection.get(i));
                }
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).getSeats().add(inbound2SeatSelection.get(i));
                }
                outbound1Reservation.setPassengers(passengers);
                outbound1Reservation.getFlightSchedules().add(outbound1FlightSchedule);
                outbound1Reservation.getFlightSchedules().add(outbound2FlightSchedule);
                outbound1Reservation.getFlightSchedules().add(inbound1FlightSchedule);
                outbound1Reservation.getFlightSchedules().add(inbound2FlightSchedule);

                for (CabinClass cabinClass : outbound1FlightSchedule.getSeatInventory().getAllCabinClasses()) {
                    outbound1Reservation.getCabinClassIds().add(cabinClass.getCabinClassId());
                }
                for (CabinClass cabinClass : outbound2FlightSchedule.getSeatInventory().getAllCabinClasses()) {
                    outbound1Reservation.getCabinClassIds().add(cabinClass.getCabinClassId());
                }
                for (CabinClass cabinClass : inbound1FlightSchedule.getSeatInventory().getAllCabinClasses()) {
                    outbound1Reservation.getCabinClassIds().add(cabinClass.getCabinClassId());
                }
                for (CabinClass cabinClass : inbound2FlightSchedule.getSeatInventory().getAllCabinClasses()) {
                    outbound1Reservation.getCabinClassIds().add(cabinClass.getCabinClassId());
                }

                seatInventorySessionBeanRemote.setReserved(outbound1FlightSchedule.getSeatInventory(), outbound1Id, outbound1SeatSelection);
                seatInventorySessionBeanRemote.setReserved(outbound2FlightSchedule.getSeatInventory(), outbound2Id, outbound2SeatSelection);
                seatInventorySessionBeanRemote.setReserved(inbound1FlightSchedule.getSeatInventory(), inbound1Id, inbound1SeatSelection);
                seatInventorySessionBeanRemote.setReserved(inbound2FlightSchedule.getSeatInventory(), inbound2Id, inbound2SeatSelection);
                outbound1Reservation = flightReservationSessionBeanRemote.createReservation(outbound1Reservation);

                System.out.println("Flight reservation " + outbound1Reservation.getFlightReservationId() + " created successfully");

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
            Passenger newPassenger = new Passenger(firstName, lastName, passport);
            //newPassenger = passengerSessionBeanRemote.createPassenger(newPassenger);
            passengers.add(newPassenger);
        }
        return passengers;
    }

    private Pair<List<String>, Integer> getSeatBookings(SeatInventory seatInventory, String cabinClassChosenName, int numberOfPassengers) {
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

        if (cabinClasses.get(id).getCabinClassName().equals("F")) {
            type = "First Class";
        } else if (cabinClasses.get(id).getCabinClassName().equals("J")) {
            type = "Business Class";
        } else if (cabinClasses.get(id).getCabinClassName().equals("W")) {
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
            System.out.println(Arrays.toString(avail.toArray()));
            while (true) {
                System.out.println("Enter the seat number >");
                String seatNum = sc.nextLine().trim();
                int index = avail.indexOf(seatNum);
                if (index != -1) {
                    avail.remove(seatNum);
                    seatSelection.add(seatNum);
                    break;
                } else {
                    System.out.println("Invalid seat number! Please try again!");
                }
            }
        }
        return new Pair<List<String>, Integer>(seatSelection, id);
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
        currentCustomer = newCustomer;

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

    void viewFlightReservation() {
        try {
            List<FlightReservation> reservations = flightReservationSessionBeanRemote.retrieveReservationsForCustomer(currentCustomer);
            int numReservations = 1;
            for (FlightReservation fr : reservations) {
                System.out.println("    " + numReservations + ". Flight Reservation ID: " + fr.getFlightReservationId());
            }
        } catch (NoFlightReservationsException ex) {
            System.out.println("You have no flight reservations.");
        }
    }

    void viewFlightReservationDetails() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.println("Your Flight Reservations: ");
            List<FlightReservation> reservations = flightReservationSessionBeanRemote.retrieveReservationsForCustomer(currentCustomer);
            int numReservations = 1;
            for (FlightReservation fr : reservations) {
                System.out.println("   " + numReservations + ". Flight Reservation ID: " + fr.getFlightReservationId());
                numReservations++;
            }

            while (true) {
                System.out.print("Select a Flight Reservation>");
                int id = sc.nextInt() - 1;
                sc.nextLine();
                if (id >= 0 && id < reservations.size()) {
                    FlightReservation fr = reservations.get(id);
                    displayDetails(fr);
                    break;
                } else {
                    System.out.println("Invalid selection! Please enter a valid number!");
                }
            }

        } catch (NoFlightReservationsException ex) {
            System.out.println("You have no flight reservations.");
        }

    }

    void displayDetails(FlightReservation fr) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        System.out.println("Flight Reservation ID: " + fr.getFlightReservationId());

        for (int i = 0; i < fr.getFlightSchedules().size(); i++) {
            Passenger p = fr.getPassengers().get(i);
            String seat = p.getSeats().get(0);
            FlightSchedule fs = fr.getFlightSchedules().get(i);
            String origin = fs.getFlightSchedulePlan().getFlight().getFlightRoute().getOriginAirport().getAirportName();
            String destination = fs.getFlightSchedulePlan().getFlight().getFlightRoute().getDestinationAirport().getAirportName();
            String ccName = cabinClassSessionBeanRemote.retrieveCabinClassName(fr.getCabinClassIds().get(i));
            System.out.println();
            System.out.println("Flight Number: " + fs.getFlightSchedulePlan().getFlight().getFlightNumber());
            System.out.println("    Passenger Name: " + p.getFirstName() + " " + p.getLastName());
            System.out.println("    Itinerary: " + origin + " --> " + destination);
            System.out.println("               " + fs.getDepartureDateTime().format(formatter) + "      " + fs.getArrivalDateTime().format(formatter));
            System.out.println("    Cabin Class: " + ccName + "     " + "   Seat: " + seat);
            System.out.println();
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
