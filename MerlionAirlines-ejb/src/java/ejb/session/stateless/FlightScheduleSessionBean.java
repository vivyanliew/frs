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
            System.out.println("No cabin class found!");
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
     public List<Pair<FlightSchedule, FlightSchedule>> getIndirectFlightSchedules(String departureAirportCode, String destinationAirportCode, LocalDateTime departDate, LocalDateTime returnDate, String cabinPref) throws FlightNotFoundException {
        List<Pair<FlightSchedule, FlightSchedule>> schedule = new ArrayList<>();
        List<Flight[]> flightPairs = flightSessionBean.retrieveAllIndirectFlightByFlightRoute(departureAirportCode, destinationAirportCode);

        for (Object[] pair : flightPairs) {
            Flight firstFlight = (Flight) pair[0];
            Flight secondFlight = (Flight) pair[1];
            System.out.println(firstFlight.toString());
            System.out.println(secondFlight.toString());
            
            if(firstFlight.getFlightSchedulePlans().isEmpty() || secondFlight.getFlightSchedulePlans().isEmpty() ) {
                continue;
            }
            
            for (FlightSchedulePlan firstFlightPlan : firstFlight.getFlightSchedulePlans()) {
                if (firstFlightPlan.isIsDisabled()) {
                    continue;
                }

                for (FlightSchedule firstFlightSchedule : firstFlightPlan.getFlightSchedules()) {
                    for (FlightSchedulePlan secondFlightPlan : secondFlight.getFlightSchedulePlans()) {
                        if (secondFlightPlan.isIsDisabled()) {
                            continue;
                        }

                        for (FlightSchedule secondFlightSchedule : secondFlightPlan.getFlightSchedules()) {
                            boolean cabinClassMatch = false;
                            boolean flight1DateMatch = false;
                            boolean flight2DateMatch = false;

                            // Check cabin preference
                            if (cabinPref.equals("A") || cabinPreferenceMatches(firstFlightSchedule, secondFlightSchedule, cabinPref)) {
                                cabinClassMatch = true;
                            }

                            // Check if the first flight leaves on the departure date
                            if (isFlightOnDepartureDate(firstFlightSchedule, departDate)) {
                                flight1DateMatch = true;
                            }

                            // Check if the second flight leaves after the first flight and before returnd ate
                            if (isSecondFlightAfterFirst(firstFlightSchedule, secondFlightSchedule) && isSecondFlightBeforeReturn(secondFlightSchedule, returnDate)) {
                                flight2DateMatch = true;
                            }
                            
                            System.out.println("FirstFlight: " + firstFlight.toString() + " " + secondFlight.toString());
                            System.out.println("cabin class match: " + cabinClassMatch);
                            System.out.println(firstFlightSchedule.getDepartureDateTime());
                            System.out.println("flight1 date match: " + flight1DateMatch);
                            System.out.println(secondFlightSchedule.getDepartureDateTime());
                            System.out.println("flight2 date match: " + flight2DateMatch);

                            // Add to schedule if all conditions are met
                            if (cabinClassMatch && flight1DateMatch && flight2DateMatch) {
                                schedule.add(new Pair<>(firstFlightSchedule, secondFlightSchedule));
                                System.out.println("got sth inside la");
                            }
                        }
                    }
                }
            }
        }

        Collections.sort(schedule, new FlightSchedule.IndirectFlightScheduleComparator());
        System.out.println("size:" + schedule.size());
        return schedule;
    }
     private boolean isSecondFlightBeforeReturn(FlightSchedule secondFlightSchedule, LocalDateTime returnDateTime) {
        return secondFlightSchedule.getDepartureDateTime().isBefore(returnDateTime);
    }

    @Override
    public List<Pair<FlightSchedule, FlightSchedule>> getIndirectFlightSchedules(String departureAirportCode, String destinationAirportCode, LocalDateTime departDate, String cabinPref) throws FlightNotFoundException {
        List<Pair<FlightSchedule, FlightSchedule>> schedule = new ArrayList<>();
        List<Flight[]> flightPairs = flightSessionBean.retrieveAllIndirectFlightByFlightRoute(departureAirportCode, destinationAirportCode);

        for (Object[] pair : flightPairs) {
            Flight firstFlight = (Flight) pair[0];
            Flight secondFlight = (Flight) pair[1];
            System.out.println(firstFlight.toString());
            System.out.println(secondFlight.toString());
            
            if(firstFlight.getFlightSchedulePlans().isEmpty() || secondFlight.getFlightSchedulePlans().isEmpty() ) {
                continue;
            }
            
            for (FlightSchedulePlan firstFlightPlan : firstFlight.getFlightSchedulePlans()) {
                if (firstFlightPlan.isIsDisabled()) {
                    continue;
                }

                for (FlightSchedule firstFlightSchedule : firstFlightPlan.getFlightSchedules()) {
                    for (FlightSchedulePlan secondFlightPlan : secondFlight.getFlightSchedulePlans()) {
                        if (secondFlightPlan.isIsDisabled()) {
                            continue;
                        }

                        for (FlightSchedule secondFlightSchedule : secondFlightPlan.getFlightSchedules()) {
                            boolean cabinClassMatch = false;
                            boolean flight1DateMatch = false;
                            boolean flight2DateMatch = false;

                            // Check cabin preference
                            if (cabinPref.equals("A") || cabinPreferenceMatches(firstFlightSchedule, secondFlightSchedule, cabinPref)) {
                                cabinClassMatch = true;
                            }

                            // Check if the first flight leaves on the departure date
                            if (isFlightOnDepartureDate(firstFlightSchedule, departDate)) {
                                flight1DateMatch = true;
                            }

                            // Check if the second flight leaves after the first flight
                            if (isSecondFlightAfterFirst(firstFlightSchedule, secondFlightSchedule)) {
                                flight2DateMatch = true;
                            }
                            
                            System.out.println("FirstFlight: " + firstFlight.toString() + " " + secondFlight.toString());
                            System.out.println("cabin class match: " + cabinClassMatch);
                            System.out.println(firstFlightSchedule.getDepartureDateTime());
                            System.out.println("flight1 date match: " + flight1DateMatch);
                            System.out.println(secondFlightSchedule.getDepartureDateTime());
                            System.out.println("flight2 date match: " + flight2DateMatch);

                            // Add to schedule if all conditions are met
                            if (cabinClassMatch && flight1DateMatch && flight2DateMatch) {
                                schedule.add(new Pair<>(firstFlightSchedule, secondFlightSchedule));
                                System.out.println("got sth inside la");
                            }
                        }
                    }
                }
            }
        }

        Collections.sort(schedule, new FlightSchedule.IndirectFlightScheduleComparator());
        System.out.println("size:" + schedule.size());
        return schedule;
    }

    private boolean cabinPreferenceMatches(FlightSchedule firstFlightSchedule, FlightSchedule secondFlightSchedule, String cabinPref) {
        SeatInventory seatInventory1 = firstFlightSchedule.getSeatInventory();
        SeatInventory seatInventory2 = secondFlightSchedule.getSeatInventory();

        for (CabinClass cabinClass : seatInventory1.getAllCabinClasses()) {
            for (CabinClass cabin : seatInventory2.getAllCabinClasses()) {
                if (cabinClass.getCabinClassName().equals(cabinPref) && cabin.getCabinClassName().equals(cabinPref)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isFlightOnDepartureDate(FlightSchedule flightSchedule, LocalDateTime departDate) {
        if (flightSchedule.getDepartureDateTime().isEqual(departDate)) {
            return true;
        }
        if (flightSchedule.getDepartureDateTime().isAfter(departDate) && flightSchedule.getDepartureDateTime().isBefore(departDate.plusDays(1))) {
            return true;
        }
        return false;
    }

    private boolean isSecondFlightAfterFirst(FlightSchedule firstFlightSchedule, FlightSchedule secondFlightSchedule) {
        return secondFlightSchedule.getDepartureDateTime().isAfter(firstFlightSchedule.getArrivalDateTime());
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
