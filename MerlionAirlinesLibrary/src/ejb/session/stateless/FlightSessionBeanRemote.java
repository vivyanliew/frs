/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Airport;
import entity.Flight;
import entity.FlightRoute;
import entity.FlightSchedule;
import java.util.List;
import javax.ejb.Remote;
import util.exception.FlightNotFoundException;
import util.exception.NoFlightSchedulePlansException;
import util.exception.NonUniqueFlightNumException;

/**
 *
 * @author liewvivyan
 */
@Remote
public interface FlightSessionBeanRemote {
    public Flight createNewFlight(Flight flight, FlightRoute route) throws NonUniqueFlightNumException;

    public Flight createReturnFlight(Flight mainFlight, Flight returnFlight) throws NonUniqueFlightNumException;
    public List<Flight> retrieveAllFlights();
    public void deleteFlight(String flightNum) throws FlightNotFoundException;
    public Flight retrieveFlightByFlightNumber(String inputFlightNumber) throws FlightNotFoundException;
     public List<FlightSchedule> retrieveFlightSchedules(String flightNumber) throws NoFlightSchedulePlansException, FlightNotFoundException;
     public void updateFlightNumber(String newFlightNumber, Flight flight) throws NonUniqueFlightNumException;
     public List<Flight> getFlightByOD(Airport originAirport, Airport destinationAirport) throws FlightNotFoundException;
         public List<Flight[]> retrieveAllIndirectFlightByFlightRoute(String originIATACode, String destinationIATACode) throws FlightNotFoundException;

}
