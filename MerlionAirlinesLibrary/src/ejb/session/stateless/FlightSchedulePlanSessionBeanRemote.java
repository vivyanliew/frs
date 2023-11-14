/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.FlightSchedulePlan;
import javax.ejb.Remote;

/**
 *
 * @author liewvivyan
 */
@Remote
public interface FlightSchedulePlanSessionBeanRemote {
    public void deleteFlightSchedulePlan(Long flightSchedulePlanId);
    public FlightSchedulePlan createFlightSchedulePlan(FlightSchedulePlan fsp);
     public long createReturnFlightSchedulePlan(FlightSchedulePlan main, int layoverHours);
}
