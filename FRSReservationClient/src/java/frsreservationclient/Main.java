/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package frsreservationclient;

import ejb.session.stateless.AirportSessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.FlightSchedulePlanSessionBeanRemote;
import ejb.session.stateless.FlightScheduleSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author liewvivyan
 */
public class Main {

    @EJB
    private static FlightScheduleSessionBeanRemote flightScheduleSessionBeanRemote;

    @EJB
    private static FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBeanRemote;

    @EJB
    private static FlightSessionBeanRemote flightSessionBeanRemote;

    @EJB
    private static AirportSessionBeanRemote airportSessionBeanRemote;

    @EJB
    private static CustomerSessionBeanRemote customerSessionBeanRemote;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        MainApp mainApp = new MainApp(customerSessionBeanRemote,airportSessionBeanRemote,flightSessionBeanRemote,flightSchedulePlanSessionBeanRemote,flightScheduleSessionBeanRemote);
        mainApp.runApp();
    }
    
}
