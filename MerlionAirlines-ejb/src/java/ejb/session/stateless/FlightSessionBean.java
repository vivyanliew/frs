/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Flight;
import entity.FlightRoute;
import entity.FlightSchedule;
import entity.FlightSchedulePlan;
import entity.SeatInventory;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.FlightNotFoundException;
import util.exception.NoFlightSchedulePlansException;

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

    @Override
    public void deleteFlight(String flightNum) throws FlightNotFoundException {

        Flight flight = retrieveFlightByFlightNumber(flightNum);
        if (flight.getFlightSchedulePlans().size() == 0) {
            flight.getFlightRoute().getFlights().remove(flight); //remove flight from flightRoute's list
            em.remove(flight);
            if (flight.getReturnFlight()!=null) { //remove return flight
                Flight returnFlight = flight.getReturnFlight();
                em.remove(returnFlight);
            }
        } else {
            flight.setIsDisabled(true);
            if (flight.getReturnFlight()!=null) {
                Flight returnFlight = flight.getReturnFlight();
                returnFlight.setIsDisabled(true);
            }
            List<FlightSchedulePlan> flightSchedulePlans = flight.getFlightSchedulePlans();
            for (FlightSchedulePlan fsp : flightSchedulePlans) {
                fsp.setIsDisabled(true);
                for (FlightSchedule fs : fsp.getFlightSchedules()) {
                    fs.setIsDisabled(true);
                }
            }
        }
    }

    @Override
    public Flight retrieveFlightByFlightNumber(String inputFlightNumber) throws FlightNotFoundException {
        Query query = em.createQuery("SELECT f FROM Flight f WHERE f.flightNumber = :inputFlightNumber").setParameter("inputFlightNumber", inputFlightNumber);
        Flight output = (Flight) query.getSingleResult();
        if (output != null) {
            return output;
        } else {
            throw new FlightNotFoundException("Flight with the given flight number does not exist!");
        }

    }
    
    @Override
    public List<FlightSchedule> retrieveFlightSchedules(String flightNumber) throws NoFlightSchedulePlansException, FlightNotFoundException {
        Flight flight = retrieveFlightByFlightNumber(flightNumber);
        if (flight.getFlightSchedulePlans().size()!=0) {
            List<FlightSchedulePlan> fsp = flight.getFlightSchedulePlans();
        List<FlightSchedule> ans = new ArrayList<>();
        for (FlightSchedulePlan flightSchedulePlan: fsp) {
            for (FlightSchedule fs: flightSchedulePlan.getFlightSchedules()) {
                //fs.getArrivalDateTime();
                SeatInventory s = fs.getSeatInventory();
                FlightSchedule flightSchedule = s.getFlightSchedule();
                //fs.getDepartureDateTime();
                //fs.getFlightDurationHours();
                ans.add(fs);
            }
        }
        return ans;
        } else {
            throw new NoFlightSchedulePlansException("This flight has no flight schedule plans!");
        }
        
        
    }
}
