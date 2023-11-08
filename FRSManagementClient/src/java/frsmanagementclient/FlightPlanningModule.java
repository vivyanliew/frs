/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package frsmanagementclient;

import ejb.session.stateless.AircraftConfigSessionBeanRemote;
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import entity.AircraftConfig;
import entity.Employee;
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

    public FlightPlanningModule() {
    }

    public FlightPlanningModule(AircraftConfigSessionBeanRemote aircraftConfigSessionBeanRemote, FlightRouteSessionBeanRemote flightRouteSessionBeanRemote, Employee currEmployee) {
        this.aircraftConfigSessionBeanRemote = aircraftConfigSessionBeanRemote;
        this.flightRouteSessionBeanRemote = flightRouteSessionBeanRemote;
        this.currEmployee = currEmployee;
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
        System.out.println("Enter Aircraft Configuration Name> ");
        newAircraftConfig.setAircraftConfigName(sc.nextLine());
        System.out.println("Enter number of cabin classes");
        int numCabinClass = sc.nextInt();
        //for loop to create each cabin class
        //newAircraftConfig.getCabinClasses().add(cabinclass)
        //after for loop, aircraftConfigSessionBeanRemote.createaircraftconfig(newAircraftconfig)
        for (int i = 0; i < numCabinClass; i++){
            
        }
    }

}
