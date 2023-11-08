/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package frsmanagementclient;

import ejb.session.stateless.AircraftConfigSessionBeanRemote;
import ejb.session.stateless.AircraftTypeSessionBeanRemote;
import ejb.session.stateless.CabinClassSessionBeanRemote;
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import entity.AircraftConfig;
import entity.AircraftType;
import entity.CabinClass;
import entity.Employee;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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

    public FlightPlanningModule() {
    }

    public FlightPlanningModule(AircraftConfigSessionBeanRemote aircraftConfigSessionBeanRemote, FlightRouteSessionBeanRemote flightRouteSessionBeanRemote, Employee currEmployee,CabinClassSessionBeanRemote cabinClassSessionBeanRemote, AircraftTypeSessionBeanRemote aircraftTypeSessionBeanRemote) {
        this.aircraftConfigSessionBeanRemote = aircraftConfigSessionBeanRemote;
        this.flightRouteSessionBeanRemote = flightRouteSessionBeanRemote;
        this.currEmployee = currEmployee;
        this.cabinClassSessionBeanRemote = cabinClassSessionBeanRemote;
        this.aircraftTypeSessionBeanRemote = aircraftTypeSessionBeanRemote;
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
                System.out.println("4: Exit\n");
                response = 0;
                while (response < 1 || response > 4) {
                    System.out.print("> ");

                    response = sc.nextInt();

                    if (response == 1) {
                        createAircraftConfig();
                    } else if (response == 2) {

                    } else if (response == 3) {

                    } else if (response == 4) {
                        break;

                    } else {
                        System.out.println("Invalid option, please try again!\n");
                    }
                }
            }
            if (currEmployee.getUserRole() == EmployeeUserRole.ROUTEPLANNER) {
                System.out.println("1: Create Flight Route");
                System.out.println("2: View All Flight Routes");
                System.out.println("3: Delete Flight Route");
                System.out.println("4: Exit\n");
                response = 0;
                while (response < 1 || response > 4) {
                    System.out.print("> ");

                    response = sc.nextInt();

                    if (response == 1) {

                    } else if (response == 2) {

                    } else if (response == 3) {

                    } else if (response == 4) {
                        break;

                    } else {
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
        AircraftType aircraftType = aircraftTypeSessionBeanRemote.retrieveAircraftById((long)sc.nextInt());
        newAircraftConfig.setAircraftType(aircraftType);
        sc.nextLine();
        System.out.println("Enter Aircraft Configuration Name> ");
        newAircraftConfig.setAircraftConfigName(sc.nextLine());
        System.out.println("Enter number of cabin classes");
        int numCabinClass = sc.nextInt();
        newAircraftConfig.setNumCabinClasses(numCabinClass);
        
        //for loop to create each cabin class
        //newAircraftConfig.getCabinClasses().add(cabinclass)
        //after for loop, aircraftConfigSessionBeanRemote.createaircraftconfig(newAircraftconfig)
        List<CabinClass> allCabins = new ArrayList<>();
        for (int i = 0; i < numCabinClass; i++){
            //int numAisles, int numRows, int numSeatsAbreast
            CabinClass currCabinClass = new CabinClass();
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
//            int numColumn = numAisles + 1;
//            List<Integer> totalSeats = new ArrayList<>();
//            for (int j = 0; j < numColumn; j++) {
//                System.out.println("Enter number of seats for column "+ (j+1));
//                totalSeats.add(sc.nextInt());
//                //currCabinClass.getActualSeatConfigPerCol().add(sc.nextInt());
//            }
//            currCabinClass.setActualSeatConfigPerCol(totalSeats);

            currCabinClass = cabinClassSessionBeanRemote.createCabinClass(currCabinClass);
            //System.out.println("hello");
            allCabins.add(currCabinClass);
            //newAircraftConfig.getCabinClasses().add(currCabinClass);
            //System.out.println("byebye");
        }
        newAircraftConfig.setCabinClasses(allCabins);
        newAircraftConfig = aircraftConfigSessionBeanRemote.createNewAircraftConfig(newAircraftConfig);
        System.out.println("New Aircraft Configuration " + newAircraftConfig.getAircraftConfigId() + " created successfully!");
    }

}
