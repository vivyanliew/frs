/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package frsmanagementclient;

import ejb.session.stateless.AircraftConfigSessionBeanRemote;
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import ejb.session.stateless.FlightSchedulePlanSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import entity.AircraftConfig;
import entity.CabinClass;
import entity.Employee;
import entity.Fare;
import entity.Flight;
import entity.FlightRoute;
import entity.FlightSchedule;
import entity.FlightSchedule.FlightScheduleComparator;
import entity.FlightSchedulePlan;
import entity.SeatInventory;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import util.enumeration.FlightSchedulePlanType;
import util.exception.ClashingScheduleException;
import util.exception.FlightNotFoundException;
import util.exception.FlightSchedulePlanNotFoundException;
import util.exception.NonUniqueFlightNumException;

/**
 *
 * @author liewvivyan
 */
public class FlightOperationModule {

    private FlightRouteSessionBeanRemote flightRouteSessionBeanRemote;
    private AircraftConfigSessionBeanRemote aircraftConfigSessionBeanRemote;
    private FlightSessionBeanRemote flightSessionBeanRemote;
    private FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBeanRemote;

    private Employee currentEmployee;

    public FlightOperationModule() {
    }

    public FlightOperationModule(FlightRouteSessionBeanRemote flightRouteSessionBeanRemote, AircraftConfigSessionBeanRemote aircraftConfigSessionBeanRemote,
            FlightSessionBeanRemote flightSessionBeanRemote, Employee currentEmployee, FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBeanRemote) {
        this.flightRouteSessionBeanRemote = flightRouteSessionBeanRemote;
        this.aircraftConfigSessionBeanRemote = aircraftConfigSessionBeanRemote;
        this.flightSessionBeanRemote = flightSessionBeanRemote;
        this.currentEmployee = currentEmployee;
        this.flightSchedulePlanSessionBeanRemote = flightSchedulePlanSessionBeanRemote;
    }

    void mainMenu() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        while (true) {
            System.out.println("*** Flight Operation ***\n");
            System.out.println("1: Create Flight");
            System.out.println("2: View All Flights");
            System.out.println("3: View Flight Details");
            System.out.println("4: Create Flight Schedule Plan");
            System.out.println("5: View All Flight Schedule Plan");
            System.out.println("6: View Flight Schedule Plan Details");
            System.out.println("7: Exit\n");
            response = 0;
            while (response < 1 || response > 7) {
                System.out.print("> ");

                response = sc.nextInt();

                if (response == 1) {
                    try {
                        createFlight();
                    } catch (NonUniqueFlightNumException ex) {
                        System.out.println(ex.getMessage());
                    }

                } else if (response == 2) {
                    viewAllFlights();
                } else if (response == 3) {
                    viewFlightDetails();
                } else if (response == 4) {
                    createFlightSchedulePlan();

                } else if (response == 5) {
                    viewAllFlightSchedulePlans();
                } else if (response == 6) {
                    viewFlightSchedulePlanDetails();
                } else if (response == 7) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            if (response == 7) {
                break;
            }

        }
    }

    void createFlight() throws NonUniqueFlightNumException {
        Scanner sc = new Scanner(System.in);
        String flightNum = "";
        while (true) {
            System.out.print("Enter Flight No.> ");
            flightNum = sc.nextLine().trim();
            try {
                flightSessionBeanRemote.retrieveFlightByFlightNumber(flightNum);
                System.out.println("There exists a flight with the given flight number!");
                System.out.println("Please enter a unique flight number!");
            } catch (FlightNotFoundException ex) {
                break;
            }
        }

        System.out.println("Select Flight Route: ");
        List<FlightRoute> allFlightRoutes = flightRouteSessionBeanRemote.retrieveAllFlightRoutes();
        List<FlightRoute> validRoutes = new ArrayList<>();
        int count = 1;
        for (FlightRoute fr : allFlightRoutes) {
            if (!fr.isDisabled()) {
                validRoutes.add(fr);
            }
        }
        for (FlightRoute fr : validRoutes) {
            System.out.println(count + ": " + fr.getOriginAirport().getIataCode() + " -> " + fr.getDestinationAirport().getIataCode());
            count++;
        }

        System.out.print("> ");
        int selected = sc.nextInt();
        sc.nextLine();
        FlightRoute route = validRoutes.get(selected - 1);
        System.out.println("Select Aircraft Configuration: ");
        List<AircraftConfig> allAircraftConfigs = aircraftConfigSessionBeanRemote.getAllAircraftConfigs();
        count = 1;
        for (AircraftConfig ac : allAircraftConfigs) {
            System.out.println(count + ": " + ac.getAircraftConfigName());
            count++;
        }
        System.out.print("> ");
        selected = sc.nextInt();
        sc.nextLine();
        AircraftConfig aircraftConfig = allAircraftConfigs.get(selected - 1);

        Flight flight = new Flight(flightNum, route, aircraftConfig);
        flight = flightSessionBeanRemote.createNewFlight(flight, route);
        System.out.println("New Flight " + flight.getFlightId() + " is successfully created!");

        if (route.getReturnRoute() != null) {
            System.out.println("Do you want to create a return flight for this flight? Enter Y/N");
            System.out.print("> ");
            String response = "";
            response = sc.nextLine();
            if (response.equals("Y")) {
                System.out.print("Enter Flight No.> ");
                String returnFlightNum = sc.nextLine().trim();
                Flight returnFlight = new Flight(returnFlightNum, route.getReturnRoute(), aircraftConfig);
                returnFlight.setIsReturn(true);
                returnFlight = flightSessionBeanRemote.createReturnFlight(flight, returnFlight);
                System.out.println("Return Flight " + returnFlight.getFlightId() + " is successfully created!");
            }
        }
    }

    void viewAllFlights() {
        System.out.println("Flight Records in the System:");
        List<Flight> allFlights = flightSessionBeanRemote.retrieveAllFlights();
        int count = 1;
        for (Flight f : allFlights) {
            if (!f.isIsReturn()) {
                System.out.println(count + ". " + f.getFlightNumber());
                count++;
                if (f.getReturnFlight() != null) {
                    System.out.println("Return Flight: " + f.getReturnFlight().getFlightNumber());
                }
            }
        }
    }

    void viewAllFlightSchedulePlans() {
        List<FlightSchedulePlan> allPlans = flightSchedulePlanSessionBeanRemote.viewAllFlightSchedulePlans();
        System.out.println("**View All Flight Schedule Plans**");
        for (FlightSchedulePlan fsp : allPlans) {
            if (!fsp.isIsReturn()) {
                displayFlightSchedulePlan(fsp);
                System.out.println();
            }
            if (fsp.getReturnFlightSchedulePlan() != null) {
                displayReturnFlightSchedulePlan(fsp.getReturnFlightSchedulePlan());
            }
            System.out.println();
        }
    }

    private void displayFlightSchedulePlan(FlightSchedulePlan fsp) {
        System.out.println("Flight Number: " + fsp.getFlight().getFlightNumber());
        if (fsp.isIsDisabled()) {
            System.out.println("Flight Schedule Plan ID: " + fsp.getFlightSchedulePlanId() + "(disabled)");
        } else {
            System.out.println("Flight Schedule Plan ID: " + fsp.getFlightSchedulePlanId());
        }
        System.out.println("Flight Schedules: ");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM u h:mm a");

        List<FlightSchedule> allFS = fsp.getFlightSchedules();
        allFS.sort(new FlightScheduleComparator());
        int count = 1;
        for (FlightSchedule fs : allFS) {
            System.out.println("    " + count + ". Departure: " + fs.getDepartureDateTime().format(formatter));
            count++;
        }
    }

    private void displayReturnFlightSchedulePlan(FlightSchedulePlan fsp) {
        System.out.println("    Return Flight Number: " + fsp.getFlight().getFlightNumber());
        if (fsp.isIsDisabled()) {
            System.out.println("    Return Flight Schedule Plan ID: " + fsp.getFlightSchedulePlanId() + "(disabled)");
        } else {
            System.out.println("    Return Flight Schedule Plan ID: " + fsp.getFlightSchedulePlanId());
        }
        System.out.println("    Return Flight Schedules: ");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM u h:mm a");

        List<FlightSchedule> allFS = fsp.getFlightSchedules();
        allFS.sort(new FlightScheduleComparator());
        int count = 1;
        for (FlightSchedule fs : allFS) {
            System.out.println("        " + count + ". Departure: " + fs.getDepartureDateTime().format(formatter));
            count++;
        }
    }

    void viewFlightDetails() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Flight No.>");
        String flightNum = sc.nextLine().trim();
        Flight flight;
        while (true) {
            try {
                flight = flightSessionBeanRemote.retrieveFlightByFlightNumber(flightNum);
                break;
            } catch (FlightNotFoundException ex) {
                System.out.println(ex.getMessage());
                System.out.print("Please re-enter Flight No.> ");
                flightNum = sc.nextLine().trim();
            }
        }
        if (flight.isIsReturn()) {
            System.out.println("This is a RETURN flight.");
        }
        System.out.println("Origin Airport : " + flight.getFlightRoute().getOriginAirport().getAirportName());
        System.out.println("Destination Airport : " + flight.getFlightRoute().getDestinationAirport().getAirportName());
        System.out.println("Disabled : " + flight.isDisabled());
        System.out.println("Available Cabin Classes: ");
        for (CabinClass cc : flight.getAircraftConfig().getCabinClasses()) {
            System.out.println(cc.getCabinClassName());
            System.out.println("    Max Seat Capacity: " + cc.getMaxSeatCapacity());
        }
        System.out.println();
        int response = 0;

        System.out.println("Select an option :");
        System.out.println("1 : Update Flight");
        System.out.println("2 : Delete Flight");
        System.out.println("3 : Exit");
        System.out.print(">");
        response = sc.nextInt();
        sc.nextLine();
        while (true) {
//            System.out.print(">");
//            response = sc.nextInt();
//            sc.nextLine();

            if (response == 1) {
                updateFlight(flight);
                break;
            } else if (response == 2) {
                deleteFlight(flight);
                break;
            } else if (response == 3) {
                break;
            }
        }

    }

    void deleteFlight(Flight flight) {
        System.out.println("**Delete Flight**");
        try {
            flightSessionBeanRemote.deleteFlight(flight.getFlightNumber());
        } catch (FlightNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

        System.out.println("Flight " + flight.getFlightNumber() + " deleted successfully!");
    }

    void updateFlight(Flight flight) {
        Scanner sc = new Scanner(System.in);
        int response = 0;
        System.out.println("**Update Flight Details**");

        while (true) {
            System.out.println("Enter new Flight No.>");
            String newFlightNum = sc.nextLine().trim();
            try {
                flightSessionBeanRemote.updateFlightNumber(newFlightNum, flight);
                break;
            } catch (NonUniqueFlightNumException ex) {
                System.out.println(ex.getMessage());
                System.out.println("Please re-enter a valid flight number!");
            }
        }
    }

    void viewFlightSchedulePlanDetails() {
        Scanner sc = new Scanner(System.in);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM u h:mm a");

        while (true) {
            System.out.print("Enter Flight No. > ");
            String flightNum = sc.nextLine().trim();
            Flight flight;
            while (true) {
                try {
                    flight = flightSessionBeanRemote.retrieveFlightByFlightNumber(flightNum);
                    break;
                } catch (FlightNotFoundException ex) {
                    System.out.println(ex.getMessage());
                    System.out.print("Please re-enter Flight No. >");
                    flightNum = sc.nextLine().trim();
                }
            }

            System.out.println("Select a Flight Schedule Plan: ");
            List<FlightSchedulePlan> allFSP = flightSchedulePlanSessionBeanRemote.retrieveFlightSchedulePlansByFlight(flight);
            int counter = 1;
            for (FlightSchedulePlan fsp : allFSP) {
                System.out.println("    " + counter + ". Flight Schedule Plan ID: " + fsp.getFlightSchedulePlanId());
                counter++;
            }

            int index = -1;
            
            while (true) {
                System.out.print("> ");
                index = sc.nextInt() - 1;
                sc.nextLine().trim();
                if(index >=0 && index < allFSP.size()) {
                    break;
                } else {
                    System.out.println("Please enter a valid number!");
                }
            }

            FlightSchedulePlan fsp = allFSP.get(index);
            System.out.println("*Flight Schedule Plan " + fsp.getFlightSchedulePlanId() + "*");
            System.out.println("Type: " + fsp.getFlightSchedulePlanType().toString());

            System.out.println("Origin: " + fsp.getFlight().getFlightRoute().getOriginAirport().getAirportName());
            System.out.println("Destination: " + fsp.getFlight().getFlightRoute().getDestinationAirport().getAirportName());

            System.out.println("Flight Schedules: ");
            List<FlightSchedule> allFS = fsp.getFlightSchedules();
            allFS.sort(new FlightScheduleComparator());
            int count = 1;
            for (FlightSchedule fs : allFS) {
                System.out.println("    " + count + ". Departure: " + fs.getDepartureDateTime().format(formatter));
                count++;
            }

            System.out.println("Fares: ");
            List<Fare> allFares = fsp.getFares();
            for (Fare fare : allFares) {
                System.out.println("    " + fare.getCabinClass().getCabinClassName() + " " + fare.getFareAmount());
            }

            System.out.println("Select an option: ");
            System.out.println("1: Update Flight Schedule Plan");
            System.out.println("2: Delete Flight Schedule Plan");
            System.out.println("3: Exit");

            while (true) {
                System.out.print(" >");
                int response = sc.nextInt();
                sc.nextLine();

                if (response == 1) {
                    updateFlightSchedulePlan(fsp);
                    break;
                } else if (response == 2) {
                    deleteFlightSchedulePlan(fsp);
                    break;
                } else if (response == 3) {
                    break;
                }
            }
            break;

        }
    }

    private void updateFlightSchedulePlan(FlightSchedulePlan fsp) {
        Scanner sc = new Scanner(System.in);
        System.out.println("**Update Flight Schedule Plan***");
        System.out.println("Select an option: ");
        System.out.println("1: Update fares");
        System.out.println("2: Remove a flight schedule");
        System.out.println("3: Exit");
        System.out.print(">");
        int response = sc.nextInt();
        sc.nextLine().trim();

        if (response == 1) {
            updateFSPFare(fsp);
        } else if (response == 2) {
            removeAFlightSchedule(fsp);
        }
    }

    void deleteFlight() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Flight num");
        String response = sc.nextLine();
        try {
            flightSessionBeanRemote.deleteFlight(response);
        } catch (FlightNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

        System.out.println("Flight " + response + " deleted successfully!");
    }

    void deleteFlightSchedulePlan(FlightSchedulePlan fsp) {
        Scanner sc = new Scanner(System.in);
        System.out.println("**Delete Flight Schedule Plan**");
        flightSchedulePlanSessionBeanRemote.deleteFlightSchedulePlan(fsp.getFlightSchedulePlanId());
        System.out.println("Flight Schedule Plan " + fsp.getFlightSchedulePlanId() + " deleted successfully!");
    }

    private void updateFSPFare(FlightSchedulePlan fsp) {
        Scanner sc = new Scanner(System.in);
        int response = 0;

        System.out.println("Select an option: ");
        System.out.println("1: Add a new fare");
        System.out.println("2: Remove a fare");
        System.out.println("3: Exit");

        while (response < 1 || response > 3) {
            System.out.print(">");
            response = sc.nextInt();
            sc.nextLine().trim();
        }

        if (response == 1) {
            addANewFare(fsp);
        } else {
            removeAFare(fsp);
        }

    }

    private void addANewFare(FlightSchedulePlan fsp) {
        Scanner sc = new Scanner(System.in);
        System.out.println("*Add a New Fare*");
        System.out.println("Select a cabin class: ");
        int count = 1;
        for (CabinClass cc : fsp.getFlight().getAircraftConfig().getCabinClasses()) {
            System.out.println("    " + count + ". " + cc.getCabinClassName());
            count++;
        }
        System.out.print(">");
        int index = sc.nextInt() - 1;
        CabinClass cabinClass = fsp.getFlight().getAircraftConfig().getCabinClasses().get(index);
        System.out.println("Enter fare amount>");
        sc.nextLine();
        String fareString = sc.nextLine().trim();
        BigDecimal fareAmount = new BigDecimal(fareString);
        Fare fare = new Fare(fareAmount, cabinClass);
        flightSchedulePlanSessionBeanRemote.addAFare(fsp, fare);
        System.out.println("New fare added successfully!");
    }

    private void removeAFare(FlightSchedulePlan fsp) {
        Scanner sc = new Scanner(System.in);
        System.out.println("*Remove A Fare*");
        System.out.println("Select a cabin class: ");
        int count = 1;
        for (CabinClass cc : fsp.getFlight().getAircraftConfig().getCabinClasses()) {
            System.out.println("    " + count + ". " + cc.getCabinClassName());
            count++;
        }
        System.out.print(">");
        int index = sc.nextInt() - 1;
        CabinClass cabinClass = fsp.getFlight().getAircraftConfig().getCabinClasses().get(index);
        if (flightSchedulePlanSessionBeanRemote.hasMoreThanOneFare(fsp, cabinClass)) {
            System.out.println("Existing Fares: ");
            for (Fare fare : fsp.getFares()) {
                if (fare.getCabinClass().getCabinClassName().equals(cabinClass.getCabinClassName())) {
                    System.out.println("Fare ID: " + fare.getFareId());
                    System.out.println("     " + fare.getCabinClass().getCabinClassName() + ", " + fare.getFareAmount().toString());
                }
            }
            System.out.print("Enter fare ID for removal> ");
            sc.nextLine();
            String fareId = sc.nextLine().trim();
            flightSchedulePlanSessionBeanRemote.removeFare(fsp, Long.parseLong(fareId));
            System.out.println("Fare with fare ID " + fareId + " is removed successfully!");
        } else {
            System.out.println("Cabin class only has 1 existing fare! Add in a new fare before removal. ");
        }
    }

    private void removeAFlightSchedule(FlightSchedulePlan fsp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yy, h:mm a");
        Scanner sc = new Scanner(System.in);
        System.out.println("*Remove A Flight Schedule*");
        if (fsp.getFlightSchedules().size() < 2) {
            System.out.println("This flight schedule plan only has 1 flight schedule!");
            System.out.println("Try deleting the entire flight schedule plan instead.");
        } else {
            System.out.println("Existing Flight Schedules: ");
            for (FlightSchedule fs : fsp.getFlightSchedules()) {
                System.out.println("Flight Schedule ID: " + fs.getFlightScheduleId());
                System.out.println("    " + fs.getDepartureDateTime().format(formatter) + " " + fs.getArrivalDateTime().format(formatter));
            }
            System.out.print("Enter Flight Schedule ID for removal>");
            String fsId = sc.nextLine().trim();
            if (flightSchedulePlanSessionBeanRemote.removeFlightSchedule(fsp, Long.parseLong(fsId))) {
                System.out.println("Flight schedule with ID " + fsId + " is successfully deleted!");
            } else {
                System.out.println("Flight reservations have already been made for the flight schedule.");
                System.out.println("Deletion of the flight schedule is not permitted.");
            }
        }
    }

    void createFlightSchedulePlan() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Flight No.>");
        String flightNum = sc.nextLine().trim();
        Flight flight;
        while (true) {
            try {
                flight = flightSessionBeanRemote.retrieveFlightByFlightNumber(flightNum);
                break;
            } catch (FlightNotFoundException ex) {
                System.out.println(ex.getMessage());
                System.out.println("Please re-enter Flight No.>");
                flightNum = sc.nextLine().trim();
            }
        }
        FlightSchedulePlan flightSchedulePlan = new FlightSchedulePlan();
        flightSchedulePlan.setFlight(flight);

        while (true) {
            int response = 0;
            System.out.println("Select a type of schedule : ");
            System.out.println("1 : Single Schedule");
            System.out.println("2 : Manual Multiple");
            System.out.println("3 : Recurrent NDays");
            System.out.println("4 : Recurrent Weekly");
            System.out.print("> ");
            response = sc.nextInt();
            sc.nextLine();

            if (response == 1) {
                flightSchedulePlan.setFlightSchedulePlanType(FlightSchedulePlanType.SINGLE);
                FlightSchedule flightSchedule = createFlightSchedule(flight);
                flightSchedulePlan.getFlightSchedules().add(flightSchedule);
            } else if (response == 2) {
                flightSchedulePlan.setFlightSchedulePlanType(FlightSchedulePlanType.MANUALMULTIPLE);
                System.out.print("Enter number of flight schedules> ");
                int numFS = sc.nextInt();
                sc.nextLine().trim();
                for (int i = 0; i < numFS; i++) {
                    System.out.println("Flight Schedule " + (i + 1));
                    flightSchedulePlan.getFlightSchedules().add(createFlightSchedule(flight));
                }
            } else if (response == 3) {
                flightSchedulePlan.setFlightSchedulePlanType(FlightSchedulePlanType.RECURRENTNDAY);
                List<FlightSchedule> schedules = createRecurrentNFlightSchedule(flight, flightSchedulePlan);
                flightSchedulePlan.getFlightSchedules().addAll(schedules);
            } else if (response == 4) {
                flightSchedulePlan.setFlightSchedulePlanType(FlightSchedulePlanType.RECURRENTWEEKLY);
                List<FlightSchedule> schedules = createRecurrentWeeklyFlightSchedule(flight, flightSchedulePlan);
                flightSchedulePlan.getFlightSchedules().addAll(schedules);
            }

            try {
                flightSchedulePlanSessionBeanRemote.checkForClashes(flightSchedulePlan);
                break;
            } catch (ClashingScheduleException ex) {
                System.out.println(ex.getMessage());
                System.out.println("Please re-enter valid flight schedules!");
            }
        }

        List<CabinClass> cabinClasses = flight.getAircraftConfig().getCabinClasses();
        for (CabinClass c : cabinClasses) {
            System.out.print("Enter number of fares for Cabin Class " + c.getCabinClassName() + "> ");
            int numFares = sc.nextInt();
            sc.nextLine().trim();
            for (int i = 1; i < numFares + 1; i++) {
                System.out.println("    Fare " + i);
                System.out.print("      Enter fare basis code> ");
                String fareBasisCode = sc.nextLine().trim();
                System.out.print("      Enter fare amount> ");
                String fareString = sc.nextLine().trim();
                BigDecimal fareAmount = new BigDecimal(fareString);
                Fare fare = new Fare(fareAmount, c, fareBasisCode);
                flightSchedulePlan.getFares().add(fare);
            }
        }

        flightSchedulePlan = flightSchedulePlanSessionBeanRemote.createFlightSchedulePlan(flightSchedulePlan);
        System.out.println("Flight Schedule Plan " + flightSchedulePlan.getFlightSchedulePlanId() + " for Flight " + flight.getFlightNumber() + " is successfully created!");

        if (flight.getReturnFlight() != null) {
            System.out.println("Would you like to create a return flight schedule plan? (Enter Y/N)");
            System.out.print(">");
            if (sc.nextLine().trim().equals("Y")) {
                System.out.println("Enter layover duration (in hours)");
                int layover = sc.nextInt();
                sc.nextLine();
                FlightSchedulePlan returnFSP = flightSchedulePlanSessionBeanRemote.createReturnFlightSchedulePlan(flightSchedulePlan, layover);
                List<CabinClass> returnCabinClasses = flight.getReturnFlight().getAircraftConfig().getCabinClasses();
                returnFSP.getFares().addAll(flightSchedulePlan.getFares());
                flightSchedulePlanSessionBeanRemote.updateFares(returnFSP);
                System.out.println("Return Flight Schedule Plan " + returnFSP.getFlightSchedulePlanId() + " is successfully created!");
            }
        }
    }

    FlightSchedule createFlightSchedule(Flight f) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter departure date (d MMM yy)> ");
        String departDate = sc.nextLine();
        System.out.print("Enter departure time (HH:mm AM/PM)> ");
        String departTime = sc.nextLine();
        System.out.print("Enter flight duration (H Hours m Minutes)> ");
        String duration = sc.nextLine();

        LocalDateTime departDateTime = parseDateTime(departDate + ", " + departTime);
        Duration flightHours = parseDurationString(duration);

        FlightSchedule fs = new FlightSchedule(departDateTime, flightHours);
        fs.setSeatInventory(createSeatInventory(f, fs));
        return fs;
    }

    List<FlightSchedule> createRecurrentNFlightSchedule(Flight f, FlightSchedulePlan fsp) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter flight interval (in days)> ");
        int n = sc.nextInt();
        fsp.setRecurrentNDay(n);
        sc.nextLine();
        System.out.print("Enter departure time (HH:mm AM/PM)> ");
        String departTime = sc.nextLine();
        System.out.print("Enter start date (d MMM yy)> ");
        String startDate = sc.nextLine();
        System.out.print("Enter end date (d MMM yy)> ");
        String endDate = sc.nextLine();
        System.out.print("Enter flight duration (H Hours m Minutes)> ");
        String duration = sc.nextLine();

        List<FlightSchedule> schedules = new ArrayList<FlightSchedule>();
        LocalDateTime startDateTime = parseDateTime(startDate + ", " + departTime);
        fsp.setStartDate(startDateTime);
        LocalDateTime endDateTime = parseDateTime(endDate + ", " + departTime);
        fsp.setEndDate(endDateTime);

        // Create recurrent FlightSchedules
        LocalDateTime currentDateTime = startDateTime;
        while (!currentDateTime.isAfter(endDateTime)) {
            FlightSchedule fs = new FlightSchedule(currentDateTime, parseDurationString(duration));
            fs.setSeatInventory(createSeatInventory(f, fs));
            schedules.add(fs);
            currentDateTime = currentDateTime.plusDays(n);
        }

        return schedules;
    }

    List<FlightSchedule> createRecurrentWeeklyFlightSchedule(Flight f, FlightSchedulePlan fsp) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter flight day (e.g. Monday)> ");
        String dayString = sc.nextLine();
        DayOfWeek flightDay = DayOfWeek.valueOf(dayString.toUpperCase());
        fsp.setDayOfWeek(flightDay);
        System.out.print("Enter departure time (HH:mm AM/PM)> ");
        String departTime = sc.nextLine();
        System.out.print("Enter start date (d MMM yy)> ");
        String startDate = sc.nextLine();
        System.out.print("Enter end date (d MMM yy)> ");
        String endDate = sc.nextLine();
        System.out.print("Enter flight duration (H Hours m Minutes)> ");
        String duration = sc.nextLine();

        List<FlightSchedule> schedules = new ArrayList<>();
        LocalDateTime startDateTime = parseDateTime(startDate + ", " + departTime);
        fsp.setStartDate(startDateTime);
        LocalDateTime endDateTime = parseDateTime(endDate + ", " + departTime);
        fsp.setEndDate(endDateTime);
        Duration flightHours = parseDurationString(duration);

        // Calculate the difference in days between the start date and the next occurrence of the specified day
        int daysUntilFlightDay = (flightDay.getValue() - startDateTime.getDayOfWeek().getValue() + 7) % 7;

        // Create recurrent FlightSchedules
        LocalDateTime currentDateTime = startDateTime.plusDays(daysUntilFlightDay);
        while (currentDateTime.isBefore(endDateTime)) {
            FlightSchedule fs = new FlightSchedule(currentDateTime, flightHours);
            fs.setSeatInventory(createSeatInventory(f, fs));
            schedules.add(fs);
            currentDateTime = currentDateTime.plusWeeks(1);
        }

        return schedules;
    }

    SeatInventory createSeatInventory(Flight f, FlightSchedule fs) {
        SeatInventory seatInventory = new SeatInventory();
        fs.setSeatInventory(seatInventory);
        seatInventory.getAllCabinClasses().addAll(f.getAircraftConfig().getCabinClasses());
        int numCabinClasses = f.getAircraftConfig().getNumCabinClasses();

        seatInventory.setAvailableSeats(new ArrayList<List<String>>(numCabinClasses));
        seatInventory.setBalanceSeats(new ArrayList<List<String>>(numCabinClasses));
        seatInventory.setReservedSeats(new ArrayList<List<String>>(numCabinClasses));

        for (int i = 0; i < numCabinClasses; i++) {
            CabinClass c = seatInventory.getAllCabinClasses().get(i);
            int numRows = c.getNumRows();
            int numSeatsAbreast = c.getNumSeatsAbreast();
            seatInventory.getAvailableSeats().add(new ArrayList<String>(numRows * numSeatsAbreast));
            seatInventory.getReservedSeats().add(new ArrayList<String>(numRows * numSeatsAbreast));
            seatInventory.getBalanceSeats().add(new ArrayList<String>(numRows * numSeatsAbreast));
            for (int j = 1; j <= numRows; j++) {
                for (int k = 0; k < numSeatsAbreast; k++) {
                    char alphabet = (char) ('A' + k);
                    String seat = j + String.valueOf(alphabet);
                    seatInventory.getAvailableSeats().add(new ArrayList<String>());
                    seatInventory.getAvailableSeats().get(i).add(seat);
                }
            }
        }
        return seatInventory;
    }

    private static LocalDateTime parseDateTime(String dateTimeInput) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yy, h:mm a");
        return LocalDateTime.parse(dateTimeInput, formatter);
    }

    private static int parseDurationToHours(String durationInput) {
        // Split the duration string into hours and minutes
        String[] parts = durationInput.split("\\s+");
        int hours = 0;
        int minutes = 0;

        for (int i = 0; i < parts.length; i += 2) {
            int value = Integer.parseInt(parts[i]);
            if (parts[i + 1].equalsIgnoreCase("hour") || parts[i + 1].equalsIgnoreCase("hours")) {
                hours += value;
            } else if (parts[i + 1].equalsIgnoreCase("minute") || parts[i + 1].equalsIgnoreCase("minutes")) {
                minutes += value;
            }
        }

        // Convert hours and minutes to total hours
        int totalHours = hours + minutes / 60;
        return totalHours;
    }

    private static Duration parseDurationString(String durationString) {
        Pattern pattern = Pattern.compile("(\\d+)\\s*Hours?\\s*(\\d+)?\\s*Minutes?");
        Matcher matcher = pattern.matcher(durationString);

        if (matcher.matches()) {
            int hours = Integer.parseInt(matcher.group(1));
            int minutes = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;

            return Duration.ofHours(hours).plusMinutes(minutes);
        } else {
            throw new IllegalArgumentException("Invalid duration string format: " + durationString);
        }
    }

}
