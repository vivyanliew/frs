/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.CabinClass;
import entity.Fare;
import entity.Flight;
import entity.FlightSchedule;
import entity.FlightSchedulePlan;
import java.time.LocalDateTime;
import java.util.List;
import javax.ejb.Local;
import util.exception.ClashingScheduleException;
import util.exception.FlightSchedulePlanNotFoundException;

/**
 *
 * @author liewvivyan
 */
@Local
public interface FlightSchedulePlanSessionBeanLocal {

    public void deleteFlightSchedulePlan(Long flightSchedulePlanId);

    public FlightSchedulePlan createFlightSchedulePlan(FlightSchedulePlan fsp);

    public FlightSchedulePlan createReturnFlightSchedulePlan(FlightSchedulePlan main, int layoverHours);

    public List<FlightSchedulePlan> viewAllFlightSchedulePlans();
    public void checkForClashes(FlightSchedulePlan newPlan) throws ClashingScheduleException;

    public void updateFares(FlightSchedulePlan flightSchedulePlan);
    public List<FlightSchedule> retrieveSchedulesForFlight(Flight flight);

    public List<FlightSchedule> retrieveSchedulesForFlight(Flight flight, LocalDateTime departDate);
        //public FlightSchedulePlan retrieveFlightSchedulePlan(String flightNum) throws FlightSchedulePlanNotFoundException;
        public void addAFare(FlightSchedulePlan fsp, Fare fare);

    public boolean hasMoreThanOneFare(FlightSchedulePlan fsp, CabinClass cc);

    public void removeFare(FlightSchedulePlan fsp, Long fareId);

    public boolean removeFlightSchedule(FlightSchedulePlan fsp, Long fsId);
     public List<FlightSchedulePlan> retrieveFlightSchedulePlansByFlight(Flight flight);

    public FlightSchedulePlan retrieveFlightSchedulePlan(Long fspId) throws FlightSchedulePlanNotFoundException;

    
}
