/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package frsreservationclient;

import ejb.session.stateless.AirportSessionBeanRemote;
import ejb.session.stateless.CabinClassSessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.FlightReservationSessionBeanRemote;
import ejb.session.stateless.FlightSchedulePlanSessionBeanRemote;
import ejb.session.stateless.FlightScheduleSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import ejb.session.stateless.PassengerSessionBeanRemote;
import ejb.session.stateless.SeatInventorySessionBeanRemote;
import javax.ejb.EJB;
import util.exception.FlightScheduleNotFoundException;

/**
 *
 * @author liewvivyan
 */
public class Main {

    @EJB(name = "CabinClassSessionBeanRemote")
    private static CabinClassSessionBeanRemote cabinClassSessionBeanRemote;

    @EJB(name = "PassengerSessionBeanRemote")
    private static PassengerSessionBeanRemote passengerSessionBeanRemote;

    @EJB(name = "FlightReservationSessionBeanRemote")
    private static FlightReservationSessionBeanRemote flightReservationSessionBeanRemote;

    @EJB(name = "SeatInventorySessionBeanRemote")
    private static SeatInventorySessionBeanRemote seatInventorySessionBeanRemote;

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
    public static void main(String[] args) throws FlightScheduleNotFoundException {
        // TODO code application logic here
        MainApp mainApp = new MainApp(customerSessionBeanRemote,airportSessionBeanRemote,flightSessionBeanRemote,flightSchedulePlanSessionBeanRemote,flightScheduleSessionBeanRemote,seatInventorySessionBeanRemote,flightReservationSessionBeanRemote,passengerSessionBeanRemote,cabinClassSessionBeanRemote);
        mainApp.runApp();
    }
    
}
