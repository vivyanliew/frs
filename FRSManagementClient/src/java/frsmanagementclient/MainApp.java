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
    
    //salesManagementModule
    private SalesManagementModule salesManagementModule;
    
    public MainApp() {}
    public MainApp(EmployeeSessionBeanRemote employeeSessionBeanRemote,AircraftConfigSessionBeanRemote aircraftConfigSessionBeanRemote ,FlightRouteSessionBeanRemote flightRouteSessionBeanRemote,CabinClassSessionBeanRemote cabinClassSessionBeanRemote,AircraftTypeSessionBeanRemote aircraftTypeSessionBeanRemote,AirportSessionBeanRemote airportSessionBeanRemote){
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.aircraftConfigSessionBeanRemote = aircraftConfigSessionBeanRemote;
        this.cabinClassSessionBeanRemote = cabinClassSessionBeanRemote;
        this.flightRouteSessionBeanRemote = flightRouteSessionBeanRemote;
        this.aircraftTypeSessionBeanRemote = aircraftTypeSessionBeanRemote;
        this.airportSessionBeanRemote = airportSessionBeanRemote;
    }
    public void runApp() {
        
        Scanner sc = new Scanner(System.in);
        boolean loggedIn = false;
        
        while(!loggedIn) {
            System.out.println("***Welcome to Merlion Airlines FRS Management");
            
            try {
                doLogin();
                System.out.print("Log-in success!");
                loggedIn = true;
                flightPlanningModule = new FlightPlanningModule(aircraftConfigSessionBeanRemote,flightRouteSessionBeanRemote,currentEmployee,cabinClassSessionBeanRemote,aircraftTypeSessionBeanRemote,airportSessionBeanRemote);
                flightOperationModule = new FlightOperationModule();
                salesManagementModule = new SalesManagementModule();
                if (currentEmployee.getUserRole()==EmployeeUserRole.FLEETMANAGER||currentEmployee.getUserRole()==EmployeeUserRole.ROUTEPLANNER) {
                    flightPlanningModule.mainMenu();
                }
                
            } catch(InvalidLoginCredentialException ex) {
                System.out.println("Invalid login credential : " + ex.getMessage() + "\n");
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
