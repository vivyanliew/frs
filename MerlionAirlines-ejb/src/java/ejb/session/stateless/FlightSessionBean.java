/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Airport;
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
import util.exception.NonUniqueFlightNumException;

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
            if (flight.getReturnFlight() != null) { //remove return flight
                Flight returnFlight = flight.getReturnFlight();
                em.remove(returnFlight);
            }
        } else {
            flight.setIsDisabled(true);
            if (flight.getReturnFlight() != null) {
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
    public void updateFlightNumber(String newFlightNumber, Flight flight) throws NonUniqueFlightNumException {
        Query query = em.createQuery("SELECT f FROM Flight f WHERE f.flightNumber = :inputFlightNumber").setParameter("inputFlightNumber", newFlightNumber);
        if (query.getSingleResult() == null) {
            Flight f = em.find(Flight.class, flight.getFlightId());
            f.setFlightNumber(newFlightNumber);
        } else {
            throw new NonUniqueFlightNumException("There is an existing flight with the input flight number!");
        }
    }

    @Override
    public List<FlightSchedule> retrieveFlightSchedules(String flightNumber) throws NoFlightSchedulePlansException, FlightNotFoundException {
        Flight flight = retrieveFlightByFlightNumber(flightNumber);
        if (flight.getFlightSchedulePlans().size() != 0) {
            List<FlightSchedulePlan> fsp = flight.getFlightSchedulePlans();
            List<FlightSchedule> ans = new ArrayList<>();
            for (FlightSchedulePlan flightSchedulePlan : fsp) {
                for (FlightSchedule fs : flightSchedulePlan.getFlightSchedules()) {
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

    @Override
    public List<Flight> getFlightByOD(Airport originAirport, Airport destinationAirport) throws FlightNotFoundException {
        Query query = em.createQuery("SELECT f FROM Flight f WHERE f.flightRoute.originAirport = :origin AND f.flightRoute.destinationAirport = :destination").setParameter("origin", originAirport);
        query.setParameter("destination", destinationAirport);
        if (query.getResultList() != null) {
            return query.getResultList();
        } else {
            throw new FlightNotFoundException("Flight does not exist with corresponding origin and destination!");
        }
    }

    @Override
    public List<Flight[]> retrieveAllIndirectFlightByFlightRoute(String originIATACode, String destinationIATACode) throws FlightNotFoundException {
        Query query = em.createQuery("SELECT f1, f2 "
                + "FROM Flight f1 "
                + "JOIN Flight f2 ON f1.flightRoute.destinationAirport.iataCode = f2.flightRoute.originAirport.iataCode "
                + "WHERE f1.isDisabled = false "
                + "AND f2.isDisabled = false "
                + "AND f1.flightRoute.originAirport.iataCode = :origin "
                + "AND f2.flightRoute.destinationAirport.iataCode = :destination");
        query.setParameter("origin", originIATACode);
        query.setParameter("destination", destinationIATACode);
        List<Flight[]> result = query.getResultList();
        if (result.isEmpty()) {
            throw new FlightNotFoundException("No indirect flights with flight route from " + originIATACode + " to " + destinationIATACode + " found in system");
        }
        return result;
    }
}
