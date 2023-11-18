/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Flight;
import entity.FlightSchedule;
import entity.FlightSchedulePlan;
import java.time.LocalDateTime;
import java.util.List;
import javax.ejb.Local;
import util.exception.ClashingScheduleException;

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
    
}
