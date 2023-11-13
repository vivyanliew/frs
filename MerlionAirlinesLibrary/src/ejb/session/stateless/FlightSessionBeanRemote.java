/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Flight;
import entity.FlightRoute;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author liewvivyan
 */
@Remote
public interface FlightSessionBeanRemote {
    public Flight createNewFlight(Flight flight, FlightRoute route);

    public Flight createReturnFlight(Flight mainFlight, Flight returnFlight);
    public List<Flight> retrieveAllFlights();
    public void deleteFlight(Long flightId);
    
}
