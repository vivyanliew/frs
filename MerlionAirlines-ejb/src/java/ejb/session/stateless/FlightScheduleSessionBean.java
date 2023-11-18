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
import javafx.util.Pair;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.FlightNotFoundException;
import util.exception.FlightScheduleNotFoundException;
import util.exception.SeatInventoryNotFoundException;

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
    public List<FlightSchedule> getFlightSchedules(Airport departure, Airport destination, LocalDateTime date, String cabinPref) throws FlightNotFoundException {
        List<FlightSchedule> schedule = new ArrayList<>();
        List<Flight> flightsFound = flightSessionBean.getFlightByOD(departure, destination);
        for (Flight flight : flightsFound) {
            if (flight.isDisabled()) {
                continue;
            }
            for (FlightSchedulePlan fsp : flight.getFlightSchedulePlans()) {
                if (fsp.isIsDisabled()) {
                    continue;
                }
                for (FlightSchedule fs : fsp.getFlightSchedules()) {
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
                    toAdd = false;

                    LocalDateTime flightScheduleDepartDate = fs.getDepartureDateTime();
                    LocalDateTime nextDay = date.plusDays(1);
                    if (flightScheduleDepartDate.isEqual(date)) {
                        toAdd = true;
                    }
                    if (flightScheduleDepartDate.isAfter(date) && flightScheduleDepartDate.isBefore(nextDay)) {
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

    @Override
    public FlightSchedule retrieveFlightScheduleById(Long id) throws FlightScheduleNotFoundException {
        FlightSchedule fs = em.find(FlightSchedule.class, id);
        if (fs != null) {
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

        for (Fare fare : fares) {
            if (fare.getCabinClass().getCabinClassName().equals(cabinPref)) {
                ccFares.add(fare);
            }
        }
        if (ccFares.isEmpty()) {
            System.out.println("No cabin class found");
        }
        Fare smallest = ccFares.get(0);
        for (Fare fare : ccFares) {
            if (fare.getFareAmount().compareTo(smallest.getFareAmount()) < 0) {
                smallest = fare;
            }
        }
        return smallest;
    }

    @Override
    public List<Pair<FlightSchedule, FlightSchedule>> getIndirectFlightSchedules(String departureAirportCode, String destinationAirportCode, LocalDateTime departDate, String cabinPref) throws FlightNotFoundException {
        List<Pair<FlightSchedule, FlightSchedule>> schedule = new ArrayList<>();
        List<Flight[]> flight = flightSessionBean.retrieveAllIndirectFlightByFlightRoute(departureAirportCode, destinationAirportCode);
        for (Object[] pair : flight) {
            Flight firstFlight = (Flight) pair[0];
            Flight secondFlight = (Flight) pair[1];
            for (FlightSchedulePlan flightSchedulePlan : firstFlight.getFlightSchedulePlans()) {
                if (flightSchedulePlan.isIsDisabled()) {
                    continue;
                }
                for (FlightSchedule flightSchedule : flightSchedulePlan.getFlightSchedules()) {
                    for (FlightSchedulePlan fsp : secondFlight.getFlightSchedulePlans()) {
                        if (fsp.isIsDisabled()) {
                            continue;
                        }
                        for (FlightSchedule flightSchedule2 : fsp.getFlightSchedules()) {
                            boolean toAdd = false;
                            if (cabinPref.equals("A")) { //check cabin preference
                                toAdd = true;
                            } else {
                                SeatInventory seatInventory1 = flightSchedule.getSeatInventory();
                                SeatInventory seatInventory2 = flightSchedule2.getSeatInventory();
                                for (CabinClass cabinClass : seatInventory1.getAllCabinClasses()) {
                                    for (CabinClass cabin : seatInventory2.getAllCabinClasses()) {
                                        if (cabinClass.getCabinClassName().equals(cabinPref) && cabin.getCabinClassName().equals(cabinPref)) {
                                            toAdd = true;
                                        }
                                    }

                                }
                            }
                            toAdd = false;
                            //check if flight leaves on departdate
                            LocalDateTime firstFlightDepartDate = flightSchedule.getDepartureDateTime();
                            LocalDateTime nextDay = departDate.plusDays(1);
                            if (firstFlightDepartDate.isEqual(departDate)) {
                                toAdd = true;
                            }
                            if (firstFlightDepartDate.isAfter(departDate) && firstFlightDepartDate.isBefore(nextDay)) {
                                toAdd = true;
                            }
                            
                            //second flight leaves after first flight
                            toAdd = false;
                            LocalDateTime secondFlightDepartDate = flightSchedule2.getDepartureDateTime();
                            if (secondFlightDepartDate.isAfter(flightSchedule.getArrivalDateTime()) || secondFlightDepartDate.isEqual(flightSchedule.getArrivalDateTime())) {
                                toAdd = true;
                            }
                            if (toAdd) {
                                schedule.add(new Pair(flightSchedule,flightSchedule2));
                            }
                        }
                    }
                }
            }
        }
        Collections.sort(schedule, new FlightSchedule.IndirectFlightScheduleComparator());
        return schedule;
    }
    
    @Override
    public CabinClass getCorrectCabinClass(FlightSchedule flightSchedule, String cabinClassName) throws FlightScheduleNotFoundException, SeatInventoryNotFoundException {
        FlightSchedule flightScheduleFound = retrieveFlightScheduleById(flightSchedule.getFlightScheduleId());
        SeatInventory seatInventory = flightScheduleFound.getSeatInventory();
        for (CabinClass cabinClass: seatInventory.getAllCabinClasses()) {
            if (cabinClass.getCabinClassName().equals(cabinClassName)) {
                return cabinClass;
            }
        }
        throw new SeatInventoryNotFoundException("No such seat inventory");
    }

}
