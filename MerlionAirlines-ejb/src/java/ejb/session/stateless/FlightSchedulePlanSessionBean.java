/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.CabinClass;
import entity.Fare;
import entity.Flight;
import entity.FlightSchedule;
import entity.FlightSchedulePlan;
import entity.SeatInventory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author liewvivyan
 */
@Stateless
public class FlightSchedulePlanSessionBean implements FlightSchedulePlanSessionBeanRemote, FlightSchedulePlanSessionBeanLocal {

    @PersistenceContext(unitName = "MerlionAirlines-ejbPU")
    private EntityManager em;
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public void deleteFlightSchedulePlan(Long flightSchedulePlanId) {
        FlightSchedulePlan flightSchedulePlan = em.find(FlightSchedulePlan.class,flightSchedulePlanId);
        List<FlightSchedule> flightSchedules = flightSchedulePlan.getFlightSchedules();
        
        for (FlightSchedule fs : flightSchedules) {
            if (fs.getFlightReservations().size()==0) {
                em.remove(fs);
            } else {
                fs.setIsDisabled(true);
                fs.getFlightSchedulePlan().setIsDisabled(true);
                fs.getFlightSchedulePlan().getReturnFlightSchedulePlan().setIsDisabled(true);
            }
        }
        if (!flightSchedulePlan.isIsDisabled()) {
            em.remove(flightSchedulePlan);
            if (flightSchedulePlan.getReturnFlightSchedulePlan()!=null) {
                em.remove(flightSchedulePlan.getReturnFlightSchedulePlan());
            }
        }
        
    }
    @Override
    public FlightSchedulePlan createFlightSchedulePlan(FlightSchedulePlan fsp) {
        em.persist(fsp);
        // Setting with Flight
        Flight f = em.find(Flight.class, fsp.getFlight().getFlightId());
        f.getFlightSchedulePlans().add(fsp);
        for (FlightSchedule fs : fsp.getFlightSchedules()) {
            em.persist(fs);
            fs.setFlightSchedulePlan(fsp);
            em.persist(fs.getSeatInventory());
        }
        for (Fare fare : fsp.getFares()) {
            em.persist(fare);
            CabinClass cc = fare.getCabinClass();
            cc.getFares().add(fare);
        }
        em.flush();
        return fsp;
    }
    
    @Override
    public long createReturnFlightSchedulePlan(FlightSchedulePlan main, int layoverHours) {
        FlightSchedulePlan mainFSP = em.find(FlightSchedulePlan.class, main.getFlightSchedulePlanId());
        mainFSP.setLayoverHours(layoverHours);
        FlightSchedulePlan returnFSP = new FlightSchedulePlan();
        returnFSP.setFlightSchedulePlanType(mainFSP.getFlightSchedulePlanType());
        em.persist(returnFSP);
        createReturnFlightSchedules(mainFSP, returnFSP);
        returnFSP.setIsReturn(true);
        mainFSP.setReturnFlightSchedulePlan(returnFSP);
        em.flush();
        return returnFSP.getFlightSchedulePlanId();
    }
     private static LocalDateTime parseDateTime(String dateTimeInput) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yy, h:mm a");
        return LocalDateTime.parse(dateTimeInput, formatter);
    }
    private void createReturnFlightSchedules(FlightSchedulePlan mainFSP, FlightSchedulePlan returnFSP) {
        mainFSP = em.find(FlightSchedulePlan.class, mainFSP.getFlightSchedulePlanId());
        int numFS = mainFSP.getFlightSchedules().size();
        for (int i = 0; i < numFS; i++) {
            LocalDateTime returnDepart = mainFSP.getFlightSchedules().get(i).getArrivalDateTime().plusHours(mainFSP.getLayoverHours());
            int returnDuration = mainFSP.getFlightSchedules().get(i).getFlightDurationHours();
            
            FlightSchedule returnFS = new FlightSchedule(returnDepart, returnDuration);     
            em.persist(returnFS);
            
            returnFSP.getFlightSchedules().add(returnFS);
            returnFS.setFlightSchedulePlan(returnFSP);

            returnFSP.setFares(mainFSP.getFares());
            returnFSP.setFlight(mainFSP.getFlight().getReturnFlight());
            
            em.flush();

            returnFS.setSeatInventory(createSeatInventory(returnFSP.getFlight(), returnFS));  
        }
    }
    
    private SeatInventory createSeatInventory(Flight f, FlightSchedule fs) {
        SeatInventory seatInventory = new SeatInventory(fs);
        em.persist(seatInventory);
        //Flight fl = em.find(Flight.class, f.getFlightId());
        seatInventory.getAllCabinClasses().addAll(f.getAircraftConfig().getCabinClasses());
        int numCabinClasses = f.getAircraftConfig().getNumCabinClasses();
        
        List<List<String>> toSet = new ArrayList<List<String>>(numCabinClasses);
        seatInventory.setAvailableSeats(toSet);
   
        for (int i = 0; i < numCabinClasses; i++) {
            CabinClass c  = seatInventory.getAllCabinClasses().get(i);
            int numRows = c.getNumRows();
            int numSeatsAbreast = c.getNumSeatsAbreast();
            for(int j = 1; j <= numRows; j++) {
                for (int k = 0; k < numSeatsAbreast; k++) {
                    char alphabet = (char) ('A' + k);
                    String seat =  j + String.valueOf(alphabet);
                    
                    seatInventory.getAvailableSeats().get(i).add(seat);
                }
            }
            
            Collections.sort(seatInventory.getAvailableSeats().get(i));
        }
        return seatInventory;
    }
    
    
}
