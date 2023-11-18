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
import entity.FlightSchedulePlan;
import entity.SeatInventory;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import util.enumeration.FlightSchedulePlanType;
import util.exception.ClashingScheduleException;
import util.exception.FlightNotFoundException;
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
                    createFlight();
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

    void createFlight() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Flight No.>");
        String flightNum = sc.nextLine().trim();
        System.out.println("Select Flight Route>");
        List<FlightRoute> allFlightRoutes = flightRouteSessionBeanRemote.retrieveAllFlightRoutes();
        int count = 1;
        for (FlightRoute fr : allFlightRoutes) {
            if (!fr.isReturn()) {
                System.out.println(count + ": " + fr.getOriginAirport().getIataCode() + " -> " + fr.getDestinationAirport().getIataCode());
                count++;
            }
        }
        int selected = sc.nextInt();
        sc.nextLine();
        FlightRoute route = allFlightRoutes.get(selected - 1);
        System.out.println("Select Aircraft Configuration>");
        List<AircraftConfig> allAircraftConfigs = aircraftConfigSessionBeanRemote.getAllAircraftConfigs();
        count = 1;
        for (AircraftConfig ac : allAircraftConfigs) {
            System.out.println(count + ": " + ac.getAircraftConfigName());
            count++;
        }
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
                System.out.println("Enter Flight No.>");
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
                if (!f.isDisabled()) {
                    System.out.println(count + ". " + f.getFlightNumber());
                } else {
                    System.out.println(count + ". " + f.getFlightNumber() + " (disabled)");
                }
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
            displayFlightSchedulePlan(fsp);
            if (fsp.getReturnFlightSchedulePlan() != null) {
                displayFlightSchedulePlan(fsp.getReturnFlightSchedulePlan());
            }
        }
    }

    private void displayFlightSchedulePlan(FlightSchedulePlan fsp) {
        System.out.println("Flight Number: " + fsp.getFlight().getFlightNumber());
        System.out.println("Flight Schedule Plan ID: " + fsp.getFlightSchedulePlanId());
        System.out.println("    Flight Schedules: ");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM u h:mm a");

        List<FlightSchedule> allFS = fsp.getFlightSchedules();
        int count = 1;
        for (FlightSchedule fs : allFS) {
            System.out.println("    " + count + ". Departure: " + fs.getDepartureDateTime().format(formatter));
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

        response = sc.nextInt();
        sc.nextLine();
        if (response == 1) {
            updateFlight(flight);
        } else if (response == 2) {
            deleteFlight();
        }

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
        Integer response = 0;
        System.out.println("1: Update flight schedule plan");
        System.out.println("2: Delete flight schedule plan");
        response = sc.nextInt();
        if (response == 2) {
            deleteFlightSchedulePlan();
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

    void deleteFlightSchedulePlan() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Flight Schedule Plan id");
        Long response = sc.nextLong();
        flightSchedulePlanSessionBeanRemote.deleteFlightSchedulePlan(response);
        System.out.println("Flight Schedule Plan " + response + " deleted successfully!");
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
            System.out.println("1 : Single schedule");
            System.out.println("2 : Multiple schedules");
            System.out.println("3 : Recurrent schedules every n day");
            System.out.println("4 : Recurrent schedules every week");
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
                    flightSchedulePlan.getFlightSchedules().add(createFlightSchedule(flight));
                }
            } else if (response == 3) {
                flightSchedulePlan.setFlightSchedulePlanType(FlightSchedulePlanType.RECURRENTNDAY);
                List<FlightSchedule> schedules = createRecurrentNFlightSchedule(flight);
                flightSchedulePlan.getFlightSchedules().addAll(schedules);
            } else if (response == 4) {
                flightSchedulePlan.setFlightSchedulePlanType(FlightSchedulePlanType.RECURRENTWEEKLY);
                List<FlightSchedule> schedules = createRecurrentWeeklyFlightSchedule(flight);
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
            System.out.print("Enter number of fares for Cabin Class " + c.getCabinClassName() + ">");
            int numFares = sc.nextInt();
            sc.nextLine().trim();
            for (int i = 0; i < numFares; i++) {
                System.out.println("Fare " + (i + 1));
                System.out.println("Enter fare amount>");
                String fareString = sc.nextLine().trim();
                BigDecimal fareAmount = new BigDecimal(fareString);
                Fare fare = new Fare(fareAmount, c);
                //c.getFares().add(fare);
                flightSchedulePlan.getFares().add(fare);
            }
        }

        flightSchedulePlan = flightSchedulePlanSessionBeanRemote.createFlightSchedulePlan(flightSchedulePlan);
        System.out.println("Flight Schedule Plan " + flightSchedulePlan.getFlightSchedulePlanId() + " for Flight " + flight.getFlightNumber() + " is successfully created!");

        if (flight.getReturnFlight() != null) {
            System.out.println("Would you like to create a return flight schedule plan? (Enter Y/N)");
            System.out.println(">");
            if (sc.nextLine().trim().equals("Y")) {
                System.out.println("Enter layover duration (in hours)");
                int layover = sc.nextInt();
                FlightSchedulePlan returnFSP = flightSchedulePlanSessionBeanRemote.createReturnFlightSchedulePlan(flightSchedulePlan, layover);
                 List<CabinClass> returnCabinClasses = flight.getReturnFlight().getAircraftConfig().getCabinClasses();
                 for (CabinClass c : returnCabinClasses) {
                    System.out.print("Enter number of fares for Cabin Class " + c.getCabinClassName() + ">");
                    int numFares = sc.nextInt();
                    sc.nextLine().trim();
                    for (int i = 0; i < numFares; i++) {
                        System.out.println("Fare " + (i + 1));
                        System.out.println("Enter fare amount>");
                        String fareString = sc.nextLine().trim();
                        BigDecimal fareAmount = new BigDecimal(fareString);
                        Fare fare = new Fare(fareAmount, c);
                        //c.getFares().add(fare);
                        returnFSP.getFares().add(fare);
                    }
                }
                 flightSchedulePlanSessionBeanRemote.updateFares(returnFSP);
                //Long returnFSPId = flightSchedulePlanSessionBeanRemote.createReturnFlightSchedulePlan(flightSchedulePlan, layover);
                System.out.println("Return Flight Schedule Plan " + returnFSP.getFlightSchedulePlanId() + " is successfully created!");
            }
        }
    }

    FlightSchedule createFlightSchedule(Flight f) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter departure date (d MMM yy) (eg. 1 Jan 23) > ");
        String departDate = sc.nextLine();
        System.out.print("Enter departure time (HH:mm AM/PM) (eg. 12:45 AM) > ");
        String departTime = sc.nextLine();
        System.out.print("Enter flight duration (H Hours m Minute) (eg. 12 Hours 10 Minute) > ");
        String duration = sc.nextLine();

        LocalDateTime departDateTime = parseDateTime(departDate + ", " + departTime);
        int flightHours = parseDurationToHours(duration);

        FlightSchedule fs = new FlightSchedule(departDateTime, flightHours);
        fs.setSeatInventory(createSeatInventory(f, fs));
        return fs;
    }

    List<FlightSchedule> createRecurrentNFlightSchedule(Flight f) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter flight interval (in days)> ");
        int n = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter departure time (HH:mm AM/PM)> ");
        String departTime = sc.nextLine();
        System.out.print("Enter start date (d MMM yy)> ");
        String startDate = sc.nextLine();
        System.out.print("Enter end date (d MMM yy)> ");
        String endDate = sc.nextLine();
        System.out.print("Enter flight duration (H Hours m Minute)> ");
        String duration = sc.nextLine();

        List<FlightSchedule> schedules = new ArrayList<FlightSchedule>();
        LocalDateTime startDateTime = parseDateTime(startDate + ", " + departTime);
        LocalDateTime endDateTime = parseDateTime(endDate + ", " + departTime);

        // Create recurrent FlightSchedules
        LocalDateTime currentDateTime = startDateTime; // !currentDateTime.isAfter(endDateTime)
        while (currentDateTime.isBefore(endDateTime)) {
            FlightSchedule fs = new FlightSchedule(currentDateTime, parseDurationToHours(duration));
            fs.setSeatInventory(createSeatInventory(f, fs));
            schedules.add(fs);
            currentDateTime = currentDateTime.plusDays(n);
        }

        return schedules;
    }

    List<FlightSchedule> createRecurrentWeeklyFlightSchedule(Flight f) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter flight day (e.g. Monday)> ");
        String dayString = sc.nextLine();
        DayOfWeek flightDay = DayOfWeek.valueOf(dayString.toUpperCase());
        System.out.print("Enter departure time (HH:mm AM/PM)> ");
        String departTime = sc.nextLine();
        System.out.print("Enter start date (d MMM yy)> ");
        String startDate = sc.nextLine();
        System.out.print("Enter end date (d MMM yy)> ");
        String endDate = sc.nextLine();
        System.out.print("Enter flight duration (H Hours m Minute)> ");
        String duration = sc.nextLine();

        List<FlightSchedule> schedules = new ArrayList<>();
        LocalDateTime startDateTime = parseDateTime(startDate + ", " + departTime);
        LocalDateTime endDateTime = parseDateTime(endDate + ", " + departTime);
        int flightHours = parseDurationToHours(duration);

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

}
