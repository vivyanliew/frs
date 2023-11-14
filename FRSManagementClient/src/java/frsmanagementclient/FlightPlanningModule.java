/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package frsmanagementclient;

import ejb.session.stateless.AircraftConfigSessionBeanRemote;
import ejb.session.stateless.AircraftTypeSessionBeanRemote;
import ejb.session.stateless.AirportSessionBeanRemote;
import ejb.session.stateless.CabinClassSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import entity.AircraftConfig;
import entity.AircraftType;
import entity.Airport;
import entity.CabinClass;
import entity.Employee;
import entity.FlightRoute;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.ejb.EJB;
import util.enumeration.EmployeeUserRole;

/**
 *
 * @author liewvivyan
 */
public class FlightPlanningModule {

    private AircraftConfigSessionBeanRemote aircraftConfigSessionBeanRemote;
    private FlightRouteSessionBeanRemote flightRouteSessionBeanRemote;

    private Employee currEmployee;
    private CabinClassSessionBeanRemote cabinClassSessionBeanRemote;
    private AircraftType aircraftType;
    private AircraftTypeSessionBeanRemote aircraftTypeSessionBeanRemote;
    private AirportSessionBeanRemote airportSessionBeanRemote;

    public FlightPlanningModule() {
    }

    public FlightPlanningModule(AircraftConfigSessionBeanRemote aircraftConfigSessionBeanRemote, FlightRouteSessionBeanRemote flightRouteSessionBeanRemote, Employee currEmployee, CabinClassSessionBeanRemote cabinClassSessionBeanRemote, AircraftTypeSessionBeanRemote aircraftTypeSessionBeanRemote, AirportSessionBeanRemote airportSessionBeanRemote) {
        this.aircraftConfigSessionBeanRemote = aircraftConfigSessionBeanRemote;
        this.flightRouteSessionBeanRemote = flightRouteSessionBeanRemote;
        this.currEmployee = currEmployee;
        this.cabinClassSessionBeanRemote = cabinClassSessionBeanRemote;
        this.aircraftTypeSessionBeanRemote = aircraftTypeSessionBeanRemote;
        this.airportSessionBeanRemote = airportSessionBeanRemote;
    }

    void mainMenu() {
        //if currEmployee.userrole !=fleetPlanner or routeplanner throw InvalidAccessRightException
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        while (true) {
            if (currEmployee.getUserRole() == EmployeeUserRole.FLEETMANAGER) {
                System.out.println("*** Flight Planning ***\n");
                System.out.println("1: Create Aircraft Configuration");
                System.out.println("2: View All Aircraft Configurations");
                System.out.println("3: View Aircraft Configuration Details");
                System.out.println("4: Logout");               
                response = 0;
                while (response < 1 || response > 4) {
                    System.out.print("> ");

                    response = sc.nextInt();

                    if (response == 1) {
                        createAircraftConfig();
                    } else if (response == 2) {
                        viewAircraftConfigs();
                    } else if (response == 3) {
                        viewAircraftConfigDetails();
                    } else if (response == 4) {
                        break;
                       

                    } else {
                        System.out.println("Invalid option, please try again!\n");
                    }
                }
            }
            if (currEmployee.getUserRole() == EmployeeUserRole.ROUTEPLANNER) {
                System.out.println("*** Flight Planning ***\n");
                System.out.println("1: Create Flight Route");
                System.out.println("2: View All Flight Routes");
                System.out.println("3: Delete Flight Route");
                System.out.println("4: Logout"); 
                response = 0;
                while (response < 1 || response > 4) {
                    System.out.print("> ");

                    response = sc.nextInt();

                    if (response == 1) {
                        createFlightRoute();
                    } else if (response == 2) {
                        viewAllFlightRoutes();
                    } else if (response == 3) {
                        deleteFlightRoute();
                    } else if (response == 4) {
                        break;
                        

                    }  else {
                        System.out.println("Invalid option, please try again!\n");
                    }
                }
            }

            if (response == 4) {
                break;
            }

        }

    }

    void createAircraftConfig() {
        Scanner sc = new Scanner(System.in);
        AircraftConfig newAircraftConfig = new AircraftConfig();
        System.out.println("Enter Aircraft Type Id> ");
        AircraftType aircraftType = aircraftTypeSessionBeanRemote.retrieveAircraftById((long) sc.nextInt());
        newAircraftConfig.setAircraftType(aircraftType);
        sc.nextLine();
        System.out.println("Enter Aircraft Configuration Name> ");
        newAircraftConfig.setAircraftConfigName(sc.nextLine());
        System.out.println("Enter number of cabin classes");
        int numCabinClass = sc.nextInt();
        sc.nextLine().trim();
        newAircraftConfig.setNumCabinClasses(numCabinClass);

        List<CabinClass> allCabins = new ArrayList<>();
        int totalCabinClassSeats = 0;
        while (true) {
            for (int i = 1; i <= numCabinClass; i++) {
                //int numAisles, int numRows, int numSeatsAbreast
                CabinClass currCabinClass = new CabinClass();
                System.out.println("* Cabin Class " + i + " *");
                System.out.println("Enter cabin class name>");
                String cabinClassName = sc.nextLine().trim();
                currCabinClass.setCabinClassName(cabinClassName);
                System.out.println("Enter number of aisles");
                int numAisles = sc.nextInt();
                currCabinClass.setNumAisles(numAisles);
                System.out.println("Enter number of rows");
                currCabinClass.setNumRows(sc.nextInt());
                System.out.println("Enter number of seats abreast");
                currCabinClass.setNumSeatsAbreast(sc.nextInt());
                sc.nextLine();
                System.out.println("Enter actual seat configuration per column");
                currCabinClass.setActualSeatConfigPerCol(sc.nextLine());

                currCabinClass = cabinClassSessionBeanRemote.createCabinClass(currCabinClass);
                totalCabinClassSeats += currCabinClass.getMaxSeatCapacity();
                allCabins.add(currCabinClass);
            }
            if (totalCabinClassSeats > aircraftType.getMaxSeatCapacity()) {
                System.out.println("Exceeds aircraft type max seat capacity");
                System.out.println("Re-enter cabin classes");
            } else {
                break;
            }
        }

        newAircraftConfig.setCabinClasses(allCabins);
        newAircraftConfig = aircraftConfigSessionBeanRemote.createNewAircraftConfig(newAircraftConfig);
        System.out.println("New Aircraft Configuration " + newAircraftConfig.getAircraftConfigId() + " created successfully!");
    }

    void viewAircraftConfigs() {

        List<AircraftConfig> aircraftConfigs = aircraftConfigSessionBeanRemote.getAllAircraftConfigs();
        for (AircraftConfig a : aircraftConfigs) {
            System.out.println(a.getAircraftConfigName() + ": " + a.getAircraftType().getAircraftTypeName());
        }
    }

    void viewAircraftConfigDetails() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Aircraft Config Name");
        String name = sc.nextLine().trim();
        AircraftConfig a = aircraftConfigSessionBeanRemote.viewAircraftConfigDetails(name);
        System.out.println(a.getAircraftConfigName() + ": " + a.getAircraftType().getAircraftTypeName());
        System.out.println("Number of cabin classes: " + a.getCabinClasses().size());
        for (int i = 0; i < a.getNumCabinClasses(); i++) {
            CabinClass c = a.getCabinClasses().get(i);
            System.out.println("Cabin class " + c.getCabinClassName() + " details:");
            System.out.println("Number of rows : " + c.getNumRows());
            System.out.println("Number of seats abreast: " + c.getNumSeatsAbreast());
            System.out.println("Actual Seating configuration per column : " + c.getActualSeatConfigPerCol());
            System.out.println("Maximum seat capacity for this cabin: " + c.getMaxSeatCapacity());
        }
    }

    void createFlightRoute() {
        Scanner sc = new Scanner(System.in);
        String origin = "";
        String destination = "";

        System.out.println("Enter Origin Airport IATA Code>");
        origin = sc.nextLine().trim();
        System.out.println("Enter Destination Airport IATA Code>");
        destination = sc.nextLine().trim();

        Airport originAirport = airportSessionBeanRemote.retrieveAirportByIATACode(origin);
        //System.out.println("hi");
        Airport destinationAirport = airportSessionBeanRemote.retrieveAirportByIATACode(destination);

        FlightRoute flightRoute = new FlightRoute(originAirport, destinationAirport);
        flightRoute = flightRouteSessionBeanRemote.createNewFlightRoute(flightRoute);
        System.out.println("New Flight Route " + flightRoute.getFlightRouteId() + " between " + origin + " and " + destination + "!");
        System.out.println("Do you want to create a return route for this route? Enter Y/N");
        String response = "";
        response = sc.nextLine();
        if (response.equals("Y")) {
            FlightRoute fr = new FlightRoute(destinationAirport, originAirport);

            fr = flightRouteSessionBeanRemote.createNewFlightRoute(fr);

            flightRouteSessionBeanRemote.setReturnRoute(flightRoute, fr);
            System.out.println("Return Flight Route " + fr.getFlightRouteId() + " created successfully!");
        }
    }

    void viewAllFlightRoutes() {
        List<FlightRoute> sortedFlightRoutes = flightRouteSessionBeanRemote.getFlightRoutes();
        for (FlightRoute f : sortedFlightRoutes) {
            if (f.isDisabled()) {
                System.out.println(f.getOriginAirport().getIataCode() + " to " + f.getDestinationAirport().getIataCode() + "(disabled)");
            } else {
                System.out.println(f.getOriginAirport().getIataCode() + " to " + f.getDestinationAirport().getIataCode());
            }

        }
    }

    void deleteFlightRoute() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Flight Route id");
        Long response = sc.nextLong();
        flightRouteSessionBeanRemote.deleteFlightRoute(response);
        System.out.println("Flight route " + response + " deleted successfully!");
    }

}
