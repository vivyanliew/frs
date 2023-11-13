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
import entity.Employee;
import entity.Flight;
import entity.FlightRoute;
import java.util.List;
import java.util.Scanner;

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
        this.flightSchedulePlanSessionBeanRemote =flightSchedulePlanSessionBeanRemote;
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
                } else if (response ==3) {
                    viewFlightDetails();
                } 
                else if (response == 7) {
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
                System.out.println(count + ". " + f.getFlightNumber());
                count++;
                if (f.getReturnFlight() != null) {
                    System.out.println("Return Flight: " + f.getReturnFlight().getFlightNumber());
                }
            }
        }
    }
     void viewFlightDetails(){
     Scanner sc = new Scanner(System.in);
     
     }
     
     void deleteFlight() {
         Scanner sc = new Scanner(System.in);
        System.out.println("Enter Flight id");
        Long response = sc.nextLong();
        flightSessionBeanRemote.deleteFlight(response);
        System.out.println("Flight " + response + " deleted successfully!");
     }
     
     void deleteFlightSchedulePlan() {
         Scanner sc = new Scanner(System.in);
        System.out.println("Enter Flight Schedule Plan id");
        Long response = sc.nextLong();
        flightSchedulePlanSessionBeanRemote.deleteFlightSchedulePlan(response);
        System.out.println("Flight Schedule Plan " + response + " deleted successfully!");
     }

}
