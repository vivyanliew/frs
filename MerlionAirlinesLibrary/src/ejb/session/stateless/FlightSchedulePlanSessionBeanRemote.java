/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Flight;
import entity.FlightSchedule;
import entity.FlightSchedulePlan;
import java.time.LocalDateTime;
import java.util.List;
import javax.ejb.Remote;
import util.exception.ClashingScheduleException;

/**
 *
 * @author liewvivyan
 */
@Remote
public interface FlightSchedulePlanSessionBeanRemote {
    public void deleteFlightSchedulePlan(Long flightSchedulePlanId);
    public FlightSchedulePlan createFlightSchedulePlan(FlightSchedulePlan fsp);
     public FlightSchedulePlan createReturnFlightSchedulePlan(FlightSchedulePlan main, int layoverHours);
     public List<FlightSchedulePlan> viewAllFlightSchedulePlans();
     public void checkForClashes(FlightSchedulePlan newPlan) throws ClashingScheduleException;
     public void updateFares(FlightSchedulePlan flightSchedulePlan);

    public List<FlightSchedule> retrieveSchedulesForFlight(Flight flight);
     public List<FlightSchedule> retrieveSchedulesForFlight(Flight flight, LocalDateTime departDate);
}
