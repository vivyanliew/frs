/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package frsmanagementclient;

import ejb.session.stateless.AircraftConfigSessionBeanRemote;
import ejb.session.stateless.AircraftTypeSessionBeanRemote;
import ejb.session.stateless.CabinClassSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author liewvivyan
 */
public class Main {

    @EJB(name = "AircraftTypeSessionBeanRemote")
    private static AircraftTypeSessionBeanRemote aircraftTypeSessionBeanRemote;

    @EJB(name = "FlightRouteSessionBeanRemote")
    private static FlightRouteSessionBeanRemote flightRouteSessionBeanRemote;

    @EJB(name = "CabinClassSessionBeanRemote")
    private static CabinClassSessionBeanRemote cabinClassSessionBeanRemote;

    @EJB(name = "AircraftConfigSessionBeanRemote")
    private static AircraftConfigSessionBeanRemote aircraftConfigSessionBeanRemote;

    @EJB(name = "EmployeeSessionBeanRemote")
    private static EmployeeSessionBeanRemote employeeSessionBeanRemote;
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        MainApp mainApp = new MainApp(employeeSessionBeanRemote, aircraftConfigSessionBeanRemote,flightRouteSessionBeanRemote,cabinClassSessionBeanRemote,aircraftTypeSessionBeanRemote);
        mainApp.runApp();
    }
    
}
