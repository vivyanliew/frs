/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Flight;
import entity.FlightRoute;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author liewvivyan
 */
@Stateless
public class FlightSessionBean implements FlightSessionBeanRemote, FlightSessionBeanLocal {

    @PersistenceContext(unitName = "MerlionAirlines-ejbPU")
    private EntityManager em;

    @Override
    public Flight createNewFlight(Flight flight, FlightRoute route) {
        em.persist(flight);
        FlightRoute flightRoute = em.find(FlightRoute.class, route.getFlightRouteId());
        flightRoute.getFlights().add(flight);
        em.flush();
        return flight;
    }

    @Override
    public Flight createReturnFlight(Flight mainFlight, Flight returnFlight) {
        em.persist(returnFlight);
        mainFlight = em.find(Flight.class, mainFlight.getFlightId());
        mainFlight.setReturnFlight(returnFlight);
        FlightRoute returnRoute = em.find(FlightRoute.class, mainFlight.getFlightRoute().getReturnRoute().getFlightRouteId());
        returnRoute.getFlights().add(returnFlight);
        return returnFlight;
    }
    @Override
    public List<Flight> retrieveAllFlights() {
        Query query = em.createQuery("SELECT f FROM Flight f ORDER BY f.flightNumber ASC ");
        return query.getResultList();
    }

    
}
