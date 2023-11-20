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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.ClashingScheduleException;
import util.exception.FlightSchedulePlanNotFoundException;

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
        FlightSchedulePlan flightSchedulePlan = em.find(FlightSchedulePlan.class, flightSchedulePlanId);
        List<FlightSchedule> flightSchedules = flightSchedulePlan.getFlightSchedules();

        for (FlightSchedule fs : flightSchedules) {
            if (fs.getFlightReservations().size() == 0) {
                em.remove(fs);
                //flightSchedulePlan.getFlightSchedules().remove(fs);
                em.remove(fs.getSeatInventory());
            } else {
                fs.setIsDisabled(true);
                fs.getFlightSchedulePlan().setIsDisabled(true);
                fs.getFlightSchedulePlan().getReturnFlightSchedulePlan().setIsDisabled(true);
            }
        }
        if (!flightSchedulePlan.isIsDisabled()) { // if not diabled, remove it
            em.remove(flightSchedulePlan);
            Flight f = flightSchedulePlan.getFlight();
            f.getFlightSchedulePlans().remove(flightSchedulePlan);
            if (flightSchedulePlan.getReturnFlightSchedulePlan() != null) {
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
            //em.persist(fs.getSeatInventory());
            SeatInventory si = fs.getSeatInventory();
            si.setFlightSchedule(fs);
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
    public FlightSchedulePlan createReturnFlightSchedulePlan(FlightSchedulePlan main, int layoverHours) {
        FlightSchedulePlan mainFSP = em.find(FlightSchedulePlan.class, main.getFlightSchedulePlanId());
        mainFSP.setLayoverHours(layoverHours);
        FlightSchedulePlan returnFSP = new FlightSchedulePlan();
        returnFSP.setFlightSchedulePlanType(mainFSP.getFlightSchedulePlanType());
        returnFSP.setFares(main.getFares());
        em.persist(returnFSP);
        createReturnFlightSchedules(mainFSP, returnFSP);
        returnFSP.setIsReturn(true);
        mainFSP.setReturnFlightSchedulePlan(returnFSP);
        em.flush();
        return returnFSP;
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
            Duration returnDuration = mainFSP.getFlightSchedules().get(i).getFlightDurationHours();

            FlightSchedule returnFS = new FlightSchedule(returnDepart, returnDuration);
            em.persist(returnFS);

            returnFSP.getFlightSchedules().add(returnFS);
            returnFS.setFlightSchedulePlan(returnFSP);

            //returnFSP.setFares(mainFSP.getFares());
            returnFSP.setFlight(mainFSP.getFlight().getReturnFlight());

            em.flush();

            returnFS.setSeatInventory(createSeatInventory(returnFSP.getFlight(), returnFS));
        }
    }

    private SeatInventory createSeatInventory(Flight f, FlightSchedule fs) {
        FlightSchedule flightSchedule = em.find(FlightSchedule.class, fs.getFlightScheduleId());
        SeatInventory seatInventory = new SeatInventory(flightSchedule);
        em.persist(seatInventory);
        flightSchedule.setSeatInventory(seatInventory);
  
        seatInventory.getAllCabinClasses().addAll(f.getAircraftConfig().getCabinClasses());
        int numCabinClasses = f.getAircraftConfig().getNumCabinClasses();

        //ArrayList<List<String>> toSet = ;
        seatInventory.setAvailableSeats(new ArrayList<List<String>>(numCabinClasses));
         seatInventory.setBalanceSeats(new ArrayList<List<String>>(numCabinClasses));
        seatInventory.setReservedSeats(new ArrayList<List<String>>(numCabinClasses));

        for (int i = 0; i < numCabinClasses; i++) {
            CabinClass c = seatInventory.getAllCabinClasses().get(i);
            int numRows = c.getNumRows();
            int numSeatsAbreast = c.getNumSeatsAbreast();
            seatInventory.getAvailableSeats().add(new ArrayList<String>(numRows * numSeatsAbreast));
            seatInventory.getReservedSeats().add(new ArrayList<String>(numRows * numSeatsAbreast));
            seatInventory.getBalanceSeats().add(new ArrayList<String>(numRows * numSeatsAbreast));
            for (int j = 1; j <= numRows; j++) {
                for (int k = 0; k < numSeatsAbreast; k++) {
                    char alphabet = (char) ('A' + k);
                    String seat = j + String.valueOf(alphabet);

                    seatInventory.getAvailableSeats().get(i).add(seat);
                }
            }

            Collections.sort(seatInventory.getAvailableSeats().get(i));
        }
        return seatInventory;
    }
    @Override
    public void addAFare(FlightSchedulePlan fsp, Fare fare) {
        em.persist(fare);
        fare.getCabinClass().getFares().add(fare);
        FlightSchedulePlan flightSchedulePlan = em.find(FlightSchedulePlan.class, fsp.getFlightSchedulePlanId());
        flightSchedulePlan.getFares().add(fare);
        em.flush();
    }
    @Override
     public boolean hasMoreThanOneFare(FlightSchedulePlan fsp, CabinClass cc) {
        List<Fare> fares = fsp.getFares();
        int count = 0;
        for (Fare f : fares) {
            if (f.getCabinClass().getCabinClassName().equals(cc.getCabinClassName())) {
                count++;
            }
        }
        if (count == 1) {
            return false;
        }
        return true;
    }
    @Override
      public void removeFare(FlightSchedulePlan fsp, Long fareId) {
        FlightSchedulePlan flightSchedulePlan = em.find(FlightSchedulePlan.class, fsp.getFlightSchedulePlanId());
        Fare fare = em.find(Fare.class, fareId);
        flightSchedulePlan.getFares().remove(fare);
        fare.getCabinClass().getFares().remove(fare);
        em.remove(fare);
    }
    @Override
      public boolean removeFlightSchedule(FlightSchedulePlan fsp, Long fsId) {
        FlightSchedulePlan flightSchedulePlan = em.find(FlightSchedulePlan.class, fsp.getFlightSchedulePlanId());
        FlightSchedule flightSchedule = em.find(FlightSchedule.class, fsId);
        if (flightSchedule.getFlightReservations().size() != 0) {
            return false;
        } else {
            flightSchedulePlan.getFlightSchedules().remove(flightSchedule);
            em.remove(flightSchedule.getSeatInventory());
            em.remove(flightSchedule);
            return true;
        }
    }

    @Override
    public List<FlightSchedulePlan> viewAllFlightSchedulePlans() {
        String jpql = "SELECT DISTINCT fsp FROM FlightSchedulePlan fsp "
                + "JOIN fsp.flight f "
                + "LEFT JOIN FETCH fsp.returnFlightSchedulePlan returnFSP "
                + "LEFT JOIN FETCH fsp.flightSchedules fs "
                + "ORDER BY f.flightNumber ASC, fs.departureDateTime DESC";

        Query query = em.createQuery(jpql, FlightSchedulePlan.class);
        return query.getResultList();
    }

    @Override
    public void checkForClashes(FlightSchedulePlan newPlan) throws ClashingScheduleException {
        List<FlightSchedule> allFlightSchedules = retrieveSchedulesForFlight(newPlan.getFlight());
        for (FlightSchedule newFs : newPlan.getFlightSchedules()) {
            for (FlightSchedule oldFs : allFlightSchedules) {
                if (newFs.getDepartureDateTime().isAfter(oldFs.getDepartureDateTime()) && newFs.getDepartureDateTime().isBefore(oldFs.getArrivalDateTime())) {
                    throw new ClashingScheduleException("There is an existing flight schedule plan that clashes with the input!");
                } else if (newFs.getArrivalDateTime().isAfter(oldFs.getDepartureDateTime()) && newFs.getArrivalDateTime().isBefore(oldFs.getArrivalDateTime())) {
                    throw new ClashingScheduleException("There is an existing flight schedule plan that clashes with the input!");
                } else if (newFs.getDepartureDateTime().isEqual(oldFs.getDepartureDateTime())) {
                    throw new ClashingScheduleException("There is an existing flight schedule plan that clashes with the input!");
                }
            }
        }
    }
    @Override
    public List<FlightSchedulePlan> retrieveFlightSchedulePlansByFlight (Flight flight) {
        Query query = em.createQuery("SELECT fsp FROM FlightSchedulePlan fsp WHERE fsp.flight = :inputFlight").setParameter("inputFlight", flight);
        return query.getResultList();
    } 
    
    @Override
    public FlightSchedulePlan retrieveFlightSchedulePlan(Long fspId) throws FlightSchedulePlanNotFoundException {
        FlightSchedulePlan fsp = em.find(FlightSchedulePlan.class, fspId);
        if (fsp == null) {
            throw new FlightSchedulePlanNotFoundException("Flight schedule plan with ID " + fspId + " is not found!");
        }
        return fsp;
    }

    @Override
    public List<FlightSchedule> retrieveSchedulesForFlight(Flight flight) {
        Query query = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.flightSchedulePlan.flight = :inputFlight");
        query.setParameter("inputFlight", flight);
        return query.getResultList();
    }

    @Override
    public List<FlightSchedule> retrieveSchedulesForFlight(Flight flight, LocalDateTime departDate) {
//        String jpql = "SELECT fs FROM FlightSchedule fs WHERE fs.departureDateTime = :d AND fs.flightSchedulePlan.flight = :inputFlight";
//        Query query = em.createQuery(jpql, FlightSchedule.class);
//        query.setParameter("inputFlight", flight);
//        query.setParameter("d", departDate.toLocalDate());
//        return query.getResultList();
        String jpql = "SELECT fs FROM FlightSchedule fs WHERE fs.departureDateTime >= :startDate AND fs.departureDateTime < :nextDate AND fs.flightSchedulePlan.flight = :inputFlight";
        Query query = em.createQuery(jpql, FlightSchedule.class);

        // Set the start date as the beginning of the provided departDate
        LocalDateTime startDate = departDate.toLocalDate().atStartOfDay();

        // Set the next date as the day after the provided departDate
        LocalDateTime nextDate = startDate.plusDays(1);

        query.setParameter("inputFlight", flight);
        query.setParameter("startDate", startDate);
        query.setParameter("nextDate", nextDate);

        return query.getResultList();
    }

    private List<FlightSchedulePlan> retrieveAllFlightSchedulePlans() {
        Query query = em.createQuery("SELECT fsp FROM FlightSchedulePlan fsp");
        return query.getResultList();
    }

    @Override
    public void updateFares(FlightSchedulePlan flightSchedulePlan) {
        FlightSchedulePlan fsp = em.find(FlightSchedulePlan.class, flightSchedulePlan.getFlightSchedulePlanId());
        for (Fare fare : fsp.getFares()) {
            em.persist(fare);
            CabinClass cc = fare.getCabinClass();
            cc.getFares().add(fare);
        }
        em.flush();
    }

}
