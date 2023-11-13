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
import ejb.session.stateless.FlightSchedulePlanSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import entity.Employee;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import util.enumeration.EmployeeUserRole;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author liewvivyan
 */
public class MainApp {
   
    
    //login employee
    //if fleetmanager or routeplanner, got to flightplanning
    //if schedulemanager, go to flightoperation
    //if salesmanager, go to salesmanagement
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private Employee currentEmployee;
    
    //flightPlanningModule
    private FlightPlanningModule flightPlanningModule;
    private AircraftConfigSessionBeanRemote aircraftConfigSessionBeanRemote;
    private FlightRouteSessionBeanRemote flightRouteSessionBeanRemote;
    private CabinClassSessionBeanRemote cabinClassSessionBeanRemote;
    private AircraftTypeSessionBeanRemote aircraftTypeSessionBeanRemote;
    private AirportSessionBeanRemote airportSessionBeanRemote;
    
    //flightOperationModule
    private FlightOperationModule flightOperationModule;
    private FlightSessionBeanRemote flightSessionBeanRemote;
    private FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBeanRemote;
    //salesManagementModule
    private SalesManagementModule salesManagementModule;
    
    public MainApp() {}
    public MainApp(EmployeeSessionBeanRemote employeeSessionBeanRemote,AircraftConfigSessionBeanRemote aircraftConfigSessionBeanRemote ,FlightRouteSessionBeanRemote flightRouteSessionBeanRemote,CabinClassSessionBeanRemote cabinClassSessionBeanRemote,AircraftTypeSessionBeanRemote aircraftTypeSessionBeanRemote,AirportSessionBeanRemote airportSessionBeanRemote,FlightSessionBeanRemote flightSessionBeanRemote,FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBeanRemote){
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.aircraftConfigSessionBeanRemote = aircraftConfigSessionBeanRemote;
        this.cabinClassSessionBeanRemote = cabinClassSessionBeanRemote;
        this.flightRouteSessionBeanRemote = flightRouteSessionBeanRemote;
        this.aircraftTypeSessionBeanRemote = aircraftTypeSessionBeanRemote;
        this.airportSessionBeanRemote = airportSessionBeanRemote;
        this.flightSessionBeanRemote = flightSessionBeanRemote;
        this.flightSchedulePlanSessionBeanRemote = flightSchedulePlanSessionBeanRemote;
    }
    public void runApp() {
        
        Scanner sc = new Scanner(System.in);
        boolean loggedIn = false;
        String response = "";
        
        while(!loggedIn) {
            System.out.println("***Welcome to Merlion Airlines FRS Management");
            //System.out.println("Press Ctrl + Shift + DEL to exit.");
            
            try {
                doLogin();
                System.out.println("Log-in successful!");
                loggedIn = true;
                flightPlanningModule = new FlightPlanningModule(aircraftConfigSessionBeanRemote,flightRouteSessionBeanRemote,currentEmployee,cabinClassSessionBeanRemote,aircraftTypeSessionBeanRemote,airportSessionBeanRemote);
                flightOperationModule = new FlightOperationModule(flightRouteSessionBeanRemote, aircraftConfigSessionBeanRemote, 
            flightSessionBeanRemote,currentEmployee,flightSchedulePlanSessionBeanRemote);
                salesManagementModule = new SalesManagementModule();
                if (currentEmployee.getUserRole()==EmployeeUserRole.FLEETMANAGER||currentEmployee.getUserRole()==EmployeeUserRole.ROUTEPLANNER) {
                    flightPlanningModule.mainMenu();
                } else if (currentEmployee.getUserRole()==EmployeeUserRole.SCHEDULEMANAGER) {
                     flightOperationModule.mainMenu();
                }
                
            } catch(InvalidLoginCredentialException ex) {
                System.out.println("Invalid login credential : " + ex.getMessage() + "\n");
            }
            //loggedIn = false;
            System.out.println("Do you want to continue?");
            System.out.println("Press Y to continue");
            response = sc.nextLine();
            if (response.equals("Y") || response.equals("y")) {
                loggedIn = false;
            } else {
                loggedIn = true;
            }
        }
    }
    private void doLogin() throws InvalidLoginCredentialException {
        Scanner sc = new Scanner(System.in);
        String email = "";
        String password = "";
            
        System.out.println("Enter email > ");
        email = sc.nextLine().trim();
        System.out.println("Enter password > ");
        password = sc.nextLine().trim();
            
        currentEmployee = employeeSessionBeanRemote.employeeLogin(email, password);
    }
    
}
