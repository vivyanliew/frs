/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Airport;
import entity.CabinClass;
import entity.Fare;
import entity.Flight;
import entity.FlightSchedule;
import entity.FlightSchedulePlan;
import entity.SeatInventory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.FlightNotFoundException;
import util.exception.FlightScheduleNotFoundException;

/**
 *
 * @author liewvivyan
 */
@Stateless
public class FlightScheduleSessionBean implements FlightScheduleSessionBeanRemote, FlightScheduleSessionBeanLocal {

    @PersistenceContext(unitName = "MerlionAirlines-ejbPU")
    private EntityManager em;

    @EJB
    private FlightSessionBeanLocal flightSessionBean;

    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public List<FlightSchedule> getFlightSchedules(Airport departure, Airport destination, LocalDateTime date, String cabinPref) throws FlightNotFoundException{
        List<FlightSchedule> schedule = new ArrayList<>();
        List<Flight> flightsFound = flightSessionBean.getFlightByOD(departure, destination);
        for (Flight flight: flightsFound) {
            if (flight.isIsDisabled()) {
                continue;
            }
            for (FlightSchedulePlan fsp: flight.getFlightSchedulePlans()) {
                if (fsp.isIsDisabled()) {
                    continue;
                }
                for (FlightSchedule fs: fsp.getFlightSchedules()) {
                    boolean toAdd = false;
                    if (cabinPref.equals("A")) {
                        toAdd = true;
                    } else {
                        SeatInventory seatInventory = fs.getSeatInventory();
                        for (CabinClass c : seatInventory.getAllCabinClasses()) {
                            if (c.getCabinClassName().equals(cabinPref)) {
                                toAdd = true;
                            }
                        }
                    }
                    LocalDateTime flightScheduleDepartDate = fs.getDepartureDateTime();
                    LocalDateTime nextDay = date.plusDays(1);
                    if (flightScheduleDepartDate.isEqual(date)) {
                        toAdd = true;
                    }
                    if (flightScheduleDepartDate.isAfter(date) && flightScheduleDepartDate.isBefore(nextDay)){
                        toAdd = true;
                    }
                    if (toAdd) {
                        schedule.add(fs);
                    }
                    
                }
            }
        }
        Collections.sort(schedule, new FlightSchedule.FlightScheduleComparator());
        return schedule;
    }
    public FlightSchedule retrieveFlightScheduleById(Long id) throws FlightScheduleNotFoundException {
        FlightSchedule fs = em.find(FlightSchedule.class,id);
        if (fs!=null) {
            return fs;
        } else {
            throw new FlightScheduleNotFoundException("Flight Schedule " + id + " not found!");
        }
    }
    
    @Override
    public Fare getSmallestFare(FlightSchedule flightSchedule, String cabinPref) throws FlightScheduleNotFoundException {
        FlightSchedule flightScheduleFound = retrieveFlightScheduleById(flightSchedule.getFlightScheduleId());
        List<Fare> fares = flightScheduleFound.getFlightSchedulePlan().getFares();
        List<Fare> ccFares = new ArrayList<>();
        
        for (Fare fare: fares) {
            if (fare.getCabinClass().getCabinClassName().equals(cabinPref)) {
                ccFares.add(fare);
            }
        }
        if (ccFares.isEmpty()) {
            System.out.println("No cabin class found");
        }
        Fare smallest = ccFares.get(0);
        for (Fare fare : ccFares) {
            if(fare.getFareAmount().compareTo(smallest.getFareAmount()) < 0) {
                smallest = fare;
            }
        }
        return smallest;
    }

    public void persist(Object object) {
        em.persist(object);
    }
}
